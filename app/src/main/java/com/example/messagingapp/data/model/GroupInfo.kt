package com.example.messagingapp.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class GroupInfo(
    val name: String? = null,
    val tag: String? = null,
    @field:JvmField
    val isPrivate: Boolean? = null,
    val createdBy: String? = null,
    val members: List<String>? = null
)
