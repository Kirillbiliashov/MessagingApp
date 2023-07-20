package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.firebase.Chat
import com.example.messagingapp.data.model.ChatItem
import com.example.messagingapp.data.model.firebase.Message
import com.example.messagingapp.data.model.firebase.User
import com.example.messagingapp.utils.Constants.CHATS_COLL
import com.example.messagingapp.utils.Constants.LAST_MESSAGE_FIELD
import com.example.messagingapp.utils.Constants.LAST_UPDATED_FIELD
import com.example.messagingapp.utils.Constants.MEMBERS_FIELD
import com.example.messagingapp.utils.Constants.MESSAGES_COLL
import com.example.messagingapp.utils.Constants.RECEIVER_ID_FIELD
import com.example.messagingapp.utils.Constants.SENDER_ID_FIELD
import com.example.messagingapp.utils.Constants.TIMESTAMP_FIELD
import com.example.messagingapp.utils.Constants.USERS_COLL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ChatService {

    private val currUserId = auth.currentUser!!.uid

    override val userChatsFlow = callbackFlow {
        val listener = firestore.collection(CHATS_COLL)
            .whereArrayContains(MEMBERS_FIELD, currUserId)
            .orderBy(LAST_UPDATED_FIELD, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    cancel()
                } else {
                    val chats = snapshot.toObjects(Chat::class.java)
                    launch {
                        trySend(awaitAll(*chats.map {
                            async { getChatItem(it) }
                        }.toTypedArray()))
                    }

                }
            }
        awaitClose { listener.remove() }
    }

    private suspend fun getChatItem(chat: Chat): ChatItem {
        val participantId = chat.members!!.first { it != currUserId }
        val member = firestore
            .collection(USERS_COLL)
            .document(participantId)
            .get()
            .await()
            .toObject(User::class.java)
        return ChatItem(member = member, lastMessage = chat.lastMessage)
    }

    override fun getChatMessagesFlow(participantId: String) = callbackFlow {
        val listener = getMessagesQuery(participantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) cancel()
                else trySend(snapshot.toObjects(Message::class.java))
            }
        awaitClose { listener.remove() }
    }

    private fun getMessagesQuery(participantId: String) = firestore
        .collectionGroup(MESSAGES_COLL)
        .orderBy(TIMESTAMP_FIELD)
        .where(
            Filter.or(
                Filter.and(
                    Filter.equalTo(SENDER_ID_FIELD, currUserId),
                    Filter.equalTo(RECEIVER_ID_FIELD, participantId)
                ),
                Filter.and(
                    Filter.equalTo(SENDER_ID_FIELD, participantId),
                    Filter.equalTo(RECEIVER_ID_FIELD, currUserId)
                )
            )
        )

    override suspend fun saveMessage(message: Message) {
        val chatDoc = getChatDoc(message.receiverId!!)
        if (!chatDoc.isEmpty) {
            addMessageToExistingDocument(chatDoc.first().id, message)
        } else {
            createChatWithMessage(message)
        }
    }

    private suspend fun getChatDoc(participantId: String) = firestore
        .collection(CHATS_COLL)
        .whereEqualTo(MEMBERS_FIELD, arrayOf(participantId, currUserId).sorted())
        .limit(1)
        .get()
        .await()

    private suspend fun addMessageToExistingDocument(docId: String, message: Message) {
        val chatDoc = firestore.collection(CHATS_COLL).document(docId)
        val messagesColl = chatDoc.collection(MESSAGES_COLL)
        val messageId = messagesColl.document().id
        firestore.runBatch { writeBatch ->
            writeBatch.set(messagesColl.document(messageId), message)
            writeBatch.update(chatDoc, LAST_MESSAGE_FIELD, message)
            writeBatch.update(chatDoc, LAST_UPDATED_FIELD, System.currentTimeMillis())
        }.await()
    }

    private suspend fun createChatWithMessage(message: Message) {
        val chatsColl = firestore.collection(CHATS_COLL)
        val chatId = chatsColl.document().id
        val chatDoc = chatsColl.document(chatId)
        val messageId = chatDoc.collection(MESSAGES_COLL).document().id
        val messageDoc = chatDoc.collection(MESSAGES_COLL).document(messageId)
        val chat = Chat(
            members = listOf(message.receiverId!!, currUserId).sorted()
        )
        firestore.runBatch { writeBatch ->
            val currTime = System.currentTimeMillis()
            writeBatch.set(messageDoc, message)
            writeBatch.set(chatDoc, chat.copy(lastUpdated = currTime, lastMessage = message))
        }.await()
    }

}

interface ChatService {
    val userChatsFlow: Flow<List<ChatItem>>
    fun getChatMessagesFlow(participantId: String): Flow<List<Message>>
    suspend fun saveMessage(message: Message)
}