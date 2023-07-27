package com.example.messagingapp.data.model

import android.annotation.SuppressLint
import com.example.messagingapp.utils.Helpers
import com.example.messagingapp.utils.Helpers.asTimestampToString
import com.example.messagingapp.utils.Helpers.getYearFromTimestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.Instant
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

 fun Message.dateString() : String {
     val currDateYear = Helpers.currDate.asTimestampToString("yyyy.MM.dd")
     val messageDateYear = timestamp!!.asTimestampToString("yyyy.MM.dd")
     if (currDateYear == messageDateYear) return timestamp.asTimestampToString("HH:mm")
     val currYear = Helpers.currDate.getYearFromTimestamp()
     val messageYear = timestamp.getYearFromTimestamp()
     if (currYear == messageYear) return timestamp.asTimestampToString("MM.dd")
     return messageDateYear
 }