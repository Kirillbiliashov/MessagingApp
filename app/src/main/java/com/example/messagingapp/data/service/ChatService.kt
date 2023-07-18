package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.Message
import com.example.messagingapp.utils.Helpers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
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

    override fun getChatMessagesFlow(participantId: String): Flow<List<Message>> {
        val chatId = Helpers.getRelationshipId(currUserId, participantId)
        return callbackFlow {
            val messagesColl = firestore
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
            val listener = messagesColl.addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    this.cancel()
                } else {
                    val messages = snapshot.toObjects(Message::class.java)
                    trySend(messages)
                }
            }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun saveMessage(participantId: String, message: Message) {
        val chatId = Helpers.getRelationshipId(currUserId, participantId)
        firestore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message.copy(userId = currUserId))
            .await()
    }

}

interface ChatService {
    fun getChatMessagesFlow(participantId: String): Flow<List<Message>>
    suspend fun saveMessage(participantId: String, message: Message)
}
