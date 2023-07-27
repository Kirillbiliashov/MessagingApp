package com.example.messagingapp.utils

import android.annotation.SuppressLint
import java.time.Instant
import java.time.LocalDateTime
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

    fun Long.getYearFromTimestamp(): Int {
        val instant: Instant = Instant.ofEpochMilli(this)
        val localDateTime: LocalDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        return localDateTime.year
    }

}