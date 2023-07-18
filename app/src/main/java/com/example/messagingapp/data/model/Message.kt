package com.example.messagingapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
    @DocumentId val docId: String? = null,
    val content: String? = null,
    val userId: String? = null,
    val timestamp: Long? = null,
)
