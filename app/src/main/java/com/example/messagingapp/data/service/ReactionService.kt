package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.Reaction
import com.example.messagingapp.data.model.ReactionType
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReactionServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authenticationService: AuthenticationService
): ReactionService {

    private val userId = authenticationService.currentUser!!.uid

    override fun getChannelReactionsFlow(channelId: String) = callbackFlow {
        val listener = firestore.collection("reactions")
            .whereEqualTo("channelId", channelId)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) cancel()
                else trySend(snapshot.toObjects(Reaction::class.java))

            }
        awaitClose { listener.remove() }
    }

    override suspend fun addReaction(reaction: Reaction) {
        val likeReaction = getReaction(ReactionType.LIKE, reaction.postId!!)
        val dislikeReaction = getReaction(ReactionType.DISLIKE, reaction.postId)
        val likeReactionExists = !likeReaction.isEmpty
        val dislikeReactionExists = !dislikeReaction.isEmpty
        firestore.runBatch { writeBatch ->
            if (!likeReactionExists && !dislikeReactionExists) {
                writeBatch.writeNewReaction(reaction.copy(userId = userId))
            } else if (likeReactionExists) {
                writeBatch.removeLikeReaction(
                    likeReaction.documents.first().reference,
                    reaction
                )
            } else {
                writeBatch.removeDislikeReaction(
                    dislikeReaction.documents.first().reference, reaction
                )
            }
        }.await()
    }

    private fun WriteBatch.removeDislikeReaction(
        dislikeReactionDoc: DocumentReference,
        reaction: Reaction
    ) {
        this.delete(dislikeReactionDoc)
        val postDoc = getPostDoc(reaction.channelId!!, reaction.postId!!)
        this.update(postDoc, "dislikesCount", FieldValue.increment(-1))
        if (reaction.type == ReactionType.LIKE.toString()) {
            this.writeNewReaction(reaction.copy(userId = userId))
        }
    }

    private fun WriteBatch.removeLikeReaction(likeReactionDoc: DocumentReference,
                                              reaction: Reaction) {
        this.delete(likeReactionDoc)
        val postDoc = getPostDoc(reaction.channelId!!, reaction.postId!!)
        this.update(postDoc, "likesCount", FieldValue.increment(-1))
        if (reaction.type == ReactionType.DISLIKE.toString()) {
            this.writeNewReaction(reaction.copy(userId = userId))
        }
    }

    private fun WriteBatch.deleteReaction(reactionDoc: DocumentReference, reaction: Reaction) {
        this.delete(reactionDoc)
        val postDoc = getPostDoc(reaction.channelId!!, reaction.postId!!)
        val updateField = if (reaction.type!! == ReactionType.LIKE.toString())
            "likesCount" else "dislikesCount"
        this.update(postDoc, updateField, FieldValue.increment(-1))
    }


    private fun WriteBatch.writeNewReaction(reaction: Reaction) {
        val postDoc = getPostDoc(reaction.channelId!!, reaction.postId!!)
        val reactionDoc = firestore.collection("reactions").document()
        val updateField = if (reaction.type!! == ReactionType.LIKE.toString())
            "likesCount" else "dislikesCount"
        this.update(postDoc, updateField, FieldValue.increment(1))
        this.set(reactionDoc, reaction)
    }

    private fun getPostDoc(channelId: String, postId: String) = firestore
        .collection("channels")
        .document(channelId)
        .collection("posts")
        .document(postId)

    private suspend fun getReaction(type: ReactionType, postId: String) = firestore
        .collection("reactions")
        .whereEqualTo("postId", postId)
        .whereEqualTo("userId", userId)
        .whereEqualTo("type", type.toString())
        .get()
        .await()

}

interface ReactionService {
    fun getChannelReactionsFlow(channelId: String): Flow<List<Reaction>>
    suspend fun addReaction(reaction: Reaction)
}