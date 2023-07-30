package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.Channel
import com.example.messagingapp.data.model.Post
import com.example.messagingapp.utils.Constants.CHANNELS_COLL
import com.example.messagingapp.utils.Constants.LAST_POST_FIELD
import com.example.messagingapp.utils.Constants.LAST_UPDATED_FIELD
import com.example.messagingapp.utils.Constants.POSTS_COLL
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostService {

    private val channelsColl = firestore.collection(CHANNELS_COLL)

    override fun getChannelPostsFlow(channelId: String) = callbackFlow {
        val listener = channelsColl.document(channelId)
            .collection(POSTS_COLL)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) cancel()
                else trySend(snapshot.toObjects(Post::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun savePost(channelId: String, post: Post) {
        val channelRef = channelsColl.document(channelId)
        val postRef = channelRef.collection(POSTS_COLL).document()
        firestore.runBatch { writeBatch ->
            val sendTime = System.currentTimeMillis()
            val postWithTimestamp = post.copy(postedAt = sendTime)
            writeBatch.set(postRef, postWithTimestamp)
            writeBatch.update(channelRef, LAST_POST_FIELD, postWithTimestamp)
            writeBatch.update(channelRef, LAST_UPDATED_FIELD, sendTime)

        }.await()
    }

}

interface PostService {
    fun getChannelPostsFlow(channelId: String): Flow<List<Post>>
    suspend fun savePost(channelId: String, post: Post)
}