package com.example.messagingapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Channel(
    @DocumentId val docId: String? = null,
    val name: String? = null,
    val description: String? = null,
    val tag: String? = null,
    val ownerId: String? = null,
    val lastPost: Post? = null,
)
