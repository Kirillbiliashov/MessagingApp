package com.example.messagingapp.utils

object Helpers {

    fun getRelationshipId(userId1: String, userId2: String) =
        listOf(userId1, userId2).sorted().joinToString("_")
}