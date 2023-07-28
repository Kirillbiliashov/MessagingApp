package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.Channel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userProfileService: UserProfileService
) : ChannelService {


    override suspend fun saveChannel(channel: Channel) {
        val channelDoc = firestore.collection("channels").document()
        userProfileService.currentUserFlow.collectLatest { user ->
            val tags = user.channelTags ?: listOf()
            val userDoc = firestore.collection("users").document(user.docId!!)
            firestore.runBatch { writeBatch ->
                writeBatch.set(channelDoc, channel.copy(ownerId = user.docId,
                    lastUpdated = System.currentTimeMillis()))
                writeBatch.update(
                    userDoc, "channelTags", tags + channel.tag
                )
            }
        }
    }

    override suspend fun getChannelsByQuery(query: String) = if (query.length < 4) emptyList()
    else firestore.collection("channels")
        .where(
            Filter.or(
                Filter.equalTo("tag", query),
                Filter.and(
                    Filter.greaterThanOrEqualTo("name", query),
                    Filter.lessThanOrEqualTo("name", "$query\uf8ff")
                )
            )
        )
        .limit(5)
        .get()
        .await()
        .toObjects(Channel::class.java)



    @OptIn(ExperimentalCoroutinesApi::class)
    override val userChannelsFlow: Flow<List<Channel>> = userProfileService
        .currentUserFlow
        .flatMapLatest { user ->
            channelFlow {
                while (!this.isClosedForSend) {
                    send(getUserChannels(user.channelTags ?: listOf()))
                    delay(500L)
                }
            }
        }

    private suspend fun getUserChannels(channelTags: List<String>) =
        if (channelTags.isEmpty()) listOf<Channel>()
        else firestore.collection("channels")
                .whereIn("tag", channelTags)
                .orderBy("lastUpdated", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Channel::class.java)

}

interface ChannelService {
    suspend fun saveChannel(channel: Channel)
    suspend fun getChannelsByQuery(query: String): List<Channel>
    val userChannelsFlow: Flow<List<Channel>>
}