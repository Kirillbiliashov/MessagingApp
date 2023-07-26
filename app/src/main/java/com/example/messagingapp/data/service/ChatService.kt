package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.firebase.Chat
import com.example.messagingapp.data.model.firebase.Message
import com.example.messagingapp.data.model.firebase.User
import com.example.messagingapp.utils.Constants.CHATS_COLL
import com.example.messagingapp.utils.Constants.LAST_MESSAGE_FIELD
import com.example.messagingapp.utils.Constants.LAST_UPDATED_FIELD
import com.example.messagingapp.utils.Constants.MESSAGES_COLL
import com.example.messagingapp.utils.Constants.RECEIVER_ID_FIELD
import com.example.messagingapp.utils.Constants.SENDER_ID_FIELD
import com.example.messagingapp.utils.Constants.TIMESTAMP_FIELD
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userProfileService: UserProfileService
) : ChatService {

    private val currUserId = auth.currentUser!!.uid

    override val userChatsMapFlow = callbackFlow {
        val listener = userChatsQuery
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    cancel()
                } else {
                    launch {
                        trySend(
                            getChatUserMap(snapshot.toObjects(Chat::class.java))
                        )
                    }
                }
            }
        awaitClose { listener.remove() }
    }

    private val userChatsQuery = firestore
        .collection(CHATS_COLL)
        .orderBy(LAST_UPDATED_FIELD, Query.Direction.DESCENDING)
        .where(
            Filter.or(
                Filter.equalTo("lastMessage.senderId", currUserId),
                Filter.equalTo("lastMessage.receiverId", currUserId),
                Filter.and(
                    Filter.equalTo("isGroup", true),
                    Filter.equalTo("groupInfo.createdBy", currUserId)
                )
            )
        )

    private suspend fun getChatUserMap(chats: List<Chat>) = coroutineScope {
        chats.zip(
            awaitAll(
                *chats
                    .map {
                        async {
                            getChatParticipant(it.lastMessage)
                        }
                    }.toTypedArray()
            )
        ).toMap()
    }

    private suspend fun getChatParticipant(message: Message?): User? {
        if (message == null) return null
        val senderId = message.senderId
        val receiverId = message.receiverId
        val userId = if (currUserId == senderId) receiverId else senderId
        return userProfileService.getProfileByDocumentId(userId!!)
    }

    override suspend fun saveMessage(message: Message, chatId: String?): String? {
        if (chatId == null) return createChatWithMessage(message)
        addMessageToExistingDocument(message, chatId)
        return null
    }

    private suspend fun addMessageToExistingDocument(message: Message, chatId: String) {
        val chatDoc = firestore.collection(CHATS_COLL).document(chatId)
        val messagesColl = chatDoc.collection(MESSAGES_COLL)
        val messageId = messagesColl.document().id
        firestore.runBatch { writeBatch ->
            val currTime = System.currentTimeMillis()
            val messageCopy = message.copy(
                timestamp = currTime,
                senderId = currUserId
            )
            writeBatch.set(messagesColl.document(messageId), messageCopy)
            writeBatch.update(chatDoc, LAST_MESSAGE_FIELD, messageCopy)
            writeBatch.update(chatDoc, LAST_UPDATED_FIELD, currTime)
        }.await()
    }

    private suspend fun createChatWithMessage(message: Message): String {
        val chatsColl = firestore.collection(CHATS_COLL)
        val chatId = chatsColl.document().id
        val chatDoc = chatsColl.document(chatId)
        val messageId = chatDoc.collection(MESSAGES_COLL).document().id
        val messageDoc = chatDoc.collection(MESSAGES_COLL).document(messageId)
        firestore.runTransaction { transaction ->
            transaction.set(messageDoc, message)
            transaction.set(
                chatDoc, Chat(
                    lastUpdated = System.currentTimeMillis(),
                    lastMessage = message,
                    isGroup = false
                )
            )
        }.await()
        return chatId
    }

    override suspend fun createChatGroup(chat: Chat, memberIds: MutableList<String>) {
        chat.groupInfo!!["createdBy"] = currUserId
        memberIds.add(currUserId)
        val chatId = firestore
            .collection(CHATS_COLL)
            .add(chat.copy(lastUpdated = System.currentTimeMillis()))
            .await().id
        firestore.runBatch { writeBatch ->
            memberIds
                .forEach { memberId ->
                    writeBatch.set(
                        firestore
                            .collection(CHATS_COLL)
                            .document(chatId)
                            .collection("members")
                            .document(memberId), emptyMap<String, Any>()
                    )
                }
        }.await()
    }

    override suspend fun getGroupChatMembers(groupChatId: String): List<User> =
        awaitAll(
            *firestore.collection(CHATS_COLL)
                .document(groupChatId)
                .collection("members")
                .get()
                .await()
                .toObjects(User::class.java)
                .map {
                    firestore
                        .collection("users")
                        .document(it.docId!!)
                        .get()
                        .asDeferred()
                }.toTypedArray()
        ).map { it.toObject(User::class.java)!! }



    override suspend fun getByDocId(chatId: String) = firestore
        .collection(CHATS_COLL)
        .document(chatId)
        .get()
        .await()
        .toObject(Chat::class.java)!!

}

interface ChatService {
    val userChatsMapFlow: Flow<Map<Chat, User?>>
    suspend fun saveMessage(message: Message, chatId: String?): String?
    suspend fun createChatGroup(chat: Chat, memberIds: MutableList<String>)
    suspend fun getGroupChatMembers(groupChatId: String): List<User>
    suspend fun getByDocId(chatId: String): Chat
}