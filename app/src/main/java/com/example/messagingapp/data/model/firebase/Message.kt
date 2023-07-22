package com.example.messagingapp.data.model.firebase

import android.annotation.SuppressLint
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@IgnoreExtraProperties
data class Message(
    @DocumentId val docId: String? = null,
    val content: String? = null,
    val senderId: String? = null,
    val receiverId: String? = null,
    val timestamp: Long? = null,
)

@SuppressLint("NewApi")
fun Message.timestampToString(pattern: String): String {
    val instant = Instant.ofEpochMilli(this.timestamp!!)
    val formatter = DateTimeFormatter
        .ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
