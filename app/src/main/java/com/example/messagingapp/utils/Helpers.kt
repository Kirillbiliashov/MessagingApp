package com.example.messagingapp.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.messagingapp.data.model.firebase.timestampToString
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Helpers {

    val currDate: Long
        get() = System.currentTimeMillis()

    @SuppressLint("NewApi")
    fun Long.asTimestampToString(pattern: String): String {
        val instant = Instant.ofEpochMilli(this)
        val formatter = DateTimeFormatter
            .ofPattern(pattern)
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

}