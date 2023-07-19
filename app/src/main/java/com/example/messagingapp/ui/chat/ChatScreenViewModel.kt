package com.example.messagingapp.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.Message
import com.example.messagingapp.data.model.User
import com.example.messagingapp.data.service.AuthenticationService
import com.example.messagingapp.data.service.ChatService
import com.example.messagingapp.data.service.UserProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val currentMessage: String = "",
    val participant: User? = null,
    val messages: List<Message> = listOf()
)

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val userProfileService: UserProfileService,
    private val chatService: ChatService,
    private val authService: AuthenticationService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val participantId: String = requireNotNull(savedStateHandle["participantId"])
    val userId = authService.currentUser!!.uid

    private val currentMessageFlow = MutableStateFlow("")
    private val participantFlow = MutableStateFlow<User?>(null)

    val uiState = combine(
        currentMessageFlow, participantFlow,
        chatService.getChatMessagesFlow(participantId)
    ) { currentMessage, participant, messages ->
        UiState(currentMessage, participant, messages)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = UiState()
    )

    init {
        viewModelScope.launch {
            val user = userProfileService.getProfileByDocumentId(participantId)
            participantFlow.value = user
        }
    }

    fun updateMessageTextField(newValue: String) {
        currentMessageFlow.value = newValue
    }

    fun sendMessage() {
        val message = Message(
            content = currentMessageFlow.value,
            timestamp = System.currentTimeMillis(),
            receiverId = participantId,
            senderId = userId
        )
        currentMessageFlow.value = ""
        viewModelScope.launch {
            chatService.saveMessage(message)
        }
    }

}