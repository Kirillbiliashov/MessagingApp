package com.example.messagingapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
    @DocumentId val docId: String? = null,
    val lastMessage: Message? = null,
    val lastUpdated: Long? = null,
    @field:JvmField
    val isGroup: Boolean? = null,
    val groupInfo: GroupInfo? = null
)
