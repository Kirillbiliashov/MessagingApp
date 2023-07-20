package com.example.messagingapp.data.model.firebase

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
    @DocumentId val docId: String? = null,
    val members: List<String>? = null,
    val lastMessageId: String? = null,
    val lastUpdated: Long? = null
)
