package com.example.messagingapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Post(
    @DocumentId val docId: String? = null,
    val content: String? = null,
    val likesCount: Long? = null,
    val dislikesCount: Long? = null,
    val postedAt: Long? = null
)
