package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.Channel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ChannelService {

    private val currUserId = auth.currentUser!!.uid

    override suspend fun saveChannel(channel: Channel) {
        firestore.collection("channels")
            .add(channel.copy(ownerId = currUserId))
            .await()
    }

}

interface ChannelService {
    suspend fun saveChannel(channel: Channel)
}