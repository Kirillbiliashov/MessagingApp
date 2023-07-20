package com.example.messagingapp.data.model

import com.example.messagingapp.data.model.firebase.Message
import com.example.messagingapp.data.model.firebase.User

data class ChatItem(
    val member: User? = null,
    val lastMessage: Message? = null
)
