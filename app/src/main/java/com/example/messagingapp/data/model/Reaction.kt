package com.example.messagingapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Reaction(
    @DocumentId val docId: String? = null,
    val channelId: String? = null,
    val postId: String? = null,
    val userId: String? = null,
    val type: String? = null
)

enum class ReactionType {
    LIKE, DISLIKE
}
