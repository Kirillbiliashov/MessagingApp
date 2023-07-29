package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.Channel
import com.example.messagingapp.data.model.Post
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
): PostService {

    override fun getChannelPostsFlow(channelId: String) = callbackFlow {
        val listener = firestore.collection("channels").document(channelId)
            .collection("posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) cancel()
                else trySend(snapshot.toObjects(Post::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun savePost(channelId: String, post: Post) {
        val channelRef = firestore.collection("channels").document(channelId)
        val postRef = channelRef.collection("posts").document()
        firestore.runBatch { writeBatch ->
            val sendTime = System.currentTimeMillis()
            val postWithTimestamp = post.copy(postedAt = sendTime)
            writeBatch.set(postRef, postWithTimestamp)
            writeBatch.update(channelRef, "lastPost", postWithTimestamp)
            writeBatch.update(channelRef, "lastUpdated", sendTime)

        }.await()
    }

}

interface PostService {
    fun getChannelPostsFlow(channelId: String): Flow<List<Post>>
    suspend fun savePost(channelId: String, post: Post)
}