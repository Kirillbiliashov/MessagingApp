package com.example.messagingapp.ui.groupChatDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.Chat
import com.example.messagingapp.data.model.User
import com.example.messagingapp.data.service.ChatService
import com.example.messagingapp.data.service.UserProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val members: List<User> = listOf(),
    val chat: Chat? = null
)

@HiltViewModel
class GroupChatMembersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatService: ChatService,
    private val userProfileService: UserProfileService
) : ViewModel() {

    private val chatId: String = requireNotNull(savedStateHandle["chatId"])


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            val chat = chatService.getByDocId(chatId)
            val members = userProfileService.getByPhoneNumbers(chat.groupInfo!!.members!!)
            _uiState.update { it.copy(members = members, chat = chat) }
        }
    }

}