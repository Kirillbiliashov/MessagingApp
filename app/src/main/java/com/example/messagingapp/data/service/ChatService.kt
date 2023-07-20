package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.firebase.Chat
import com.example.messagingapp.data.model.ChatItem
import com.example.messagingapp.data.model.firebase.Message
import com.example.messagingapp.data.model.firebase.User
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
        val listener = firestore.collection("chats")
            .whereArrayContains("members", currUserId)
            .orderBy("lastUpdated", Query.Direction.DESCENDING)
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
        val message = firestore
            .collection("chats")
            .document(chat.docId!!)
            .collection("messages")
            .document(chat.lastMessageId!!)
            .get()
            .await()
            .toObject(Message::class.java)
        val member = firestore
            .collection("users")
            .document(participantId)
            .get()
            .await()
            .toObject(User::class.java)
        return ChatItem(member = member, lastMessage = message)
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
        .collectionGroup("messages")
        .orderBy("timestamp")
        .where(
            Filter.or(
                Filter.and(
                    Filter.equalTo("senderId", currUserId),
                    Filter.equalTo("receiverId", participantId)
                ),
                Filter.and(
                    Filter.equalTo("senderId", participantId),
                    Filter.equalTo("receiverId", currUserId)
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
        .collection("chats")
        .whereEqualTo(
            "members",
            arrayOf(participantId, currUserId).sorted()
        )
        .limit(1)
        .get()
        .await()

    private suspend fun addMessageToExistingDocument(docId: String, message: Message) {
        val chatDoc = firestore
            .collection("chats")
            .document(docId)
        val messagesColl = chatDoc.collection("messages")
        val messageId = messagesColl.document().id
        firestore.runBatch { writeBatch ->
            writeBatch.set(messagesColl.document(messageId), message)
            writeBatch.update(chatDoc, "lastMessageId", messageId)
            writeBatch.update(chatDoc, "lastUpdated", System.currentTimeMillis())
        }.await()
    }

    private suspend fun createChatWithMessage(message: Message) {
        val chatsColl = firestore.collection("chats")
        val chatId = chatsColl.document().id
        val chatDoc = chatsColl.document(chatId)
        val messageId = chatDoc.collection("messages").document().id
        val messageDoc = chatDoc.collection("messages").document(messageId)
        val chat = Chat(
            members = listOf(message.receiverId!!, currUserId).sorted(),
            lastMessageId = messageId
        )
        firestore.runBatch { writeBatch ->
            writeBatch.set(chatDoc, chat.copy(lastUpdated = System.currentTimeMillis()))
            writeBatch.set(messageDoc, message)
        }.await()
    }

}

interface ChatService {
    val userChatsFlow: Flow<List<ChatItem>>
    fun getChatMessagesFlow(participantId: String): Flow<List<Message>>
    suspend fun saveMessage(message: Message)
}