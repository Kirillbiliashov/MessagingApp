package com.example.messagingapp.ui.groupChat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.firebase.Chat
import com.example.messagingapp.data.model.firebase.Message
import com.example.messagingapp.data.model.firebase.User
import com.example.messagingapp.data.service.AuthenticationService
import com.example.messagingapp.data.service.ChatService
import com.example.messagingapp.data.service.MessageService
import com.example.messagingapp.data.service.UserProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class UiState(
    val members: List<User> = listOf(),
    val messages: List<Message> = listOf(),
    val chat: Chat? = null,
    val currentMessage: String = ""
)

@HiltViewModel
class GroupChatScreenViewModel @Inject constructor(
    private val chatService: ChatService,
    private val messageService: MessageService,
    private val authService: AuthenticationService,
    private val userProfileService: UserProfileService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var chatId: String = requireNotNull(savedStateHandle["chatId"])
    val userId = authService.currentUser!!.uid

    private val chatParticipantsFlow = MutableStateFlow<List<User>>(listOf())
    private val groupChatFlow = MutableStateFlow<Chat?>(null)
    private val currentMessageFlow = MutableStateFlow("")

    val uiState = combine(
        chatParticipantsFlow,
        messageService.getGroupChatMessagesFlow(chatId),
        groupChatFlow,
        currentMessageFlow
    ) { members, messages, chat, currentMessage ->
        UiState(members, messages, chat, currentMessage)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState()
    )

    init {
        viewModelScope.launch {
            val chat = chatService.getByDocId(chatId)
            groupChatFlow.value = chat
            chatParticipantsFlow.value =
                userProfileService.getByPhoneNumbers(chat.groupInfo!!.members!!)
        }
    }

    fun updateCurrentMessageTextField(newValue: String) {
        currentMessageFlow.value = newValue
    }

    fun sendMessage() {
        val message = Message(
            content = currentMessageFlow.value,
            receiverId = chatId
        )
        viewModelScope.launch {
            chatService.saveMessage(message, chatId)
        }
    }

}