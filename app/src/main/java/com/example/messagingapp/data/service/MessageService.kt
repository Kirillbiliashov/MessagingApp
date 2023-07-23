package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.firebase.Message
import com.example.messagingapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : MessageService {

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
        .collectionGroup(Constants.MESSAGES_COLL)
        .orderBy(Constants.TIMESTAMP_FIELD)
        .where(
            Filter.or(
                Filter.and(
                    Filter.equalTo(Constants.SENDER_ID_FIELD, currUserId),
                    Filter.equalTo(Constants.RECEIVER_ID_FIELD, participantId)
                ),
                Filter.and(
                    Filter.equalTo(Constants.SENDER_ID_FIELD, participantId),
                    Filter.equalTo(Constants.RECEIVER_ID_FIELD, currUserId)
                )
            )
        )

}

interface MessageService {
    fun getChatMessagesFlow(participantId: String): Flow<List<Message>>
}