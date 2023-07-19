package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.Chat
import com.example.messagingapp.data.model.Message
import com.example.messagingapp.utils.Helpers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ChatService {

    private val currUserId = auth.currentUser!!.uid

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
        firestore
            .collection("chats")
            .document(docId)
            .collection("messages")
            .add(message)
            .await()
    }

    private suspend fun createChatWithMessage(message: Message) {
        val chat = Chat(members = listOf(message.receiverId!!, currUserId).sorted())
        val chatRef = firestore.collection("chats").add(chat).await()
        firestore
            .collection("chats")
            .document(chatRef.id)
            .collection("messages")
            .add(message)
            .await()
    }

}

interface ChatService {
    fun getChatMessagesFlow(participantId: String): Flow<List<Message>>
    suspend fun saveMessage(message: Message)
}