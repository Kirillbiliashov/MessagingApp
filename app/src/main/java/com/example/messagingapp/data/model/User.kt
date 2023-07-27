package com.example.messagingapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    @DocumentId val docId: String? = null,
    val phoneNumber: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val description: String? = null,
    val tag: String? = null,
)


fun User.headerName(): String = if (firstName != null) "$firstName $lastName"
else phoneNumber!!