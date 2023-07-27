package com.example.messagingapp.ui.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.Chat
import com.example.messagingapp.data.model.User
import com.example.messagingapp.data.service.ChatService
import com.example.messagingapp.data.service.UserProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val searchQuery: String? = null,
    val users: List<User> = listOf(),
    val chatsMap: Map<Chat, User?> = mapOf()
)

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val userProfileService: UserProfileService,
    private val chatService: ChatService
) : ViewModel() {

    private val searchQueryFlow = MutableStateFlow<String?>(null)
    private val usersFlow = MutableStateFlow(listOf<User>())

    val uiState = combine(
        searchQueryFlow, usersFlow,
        chatService.userChatsMapFlow
    ) { searchQuery, users, chatsMap ->
        UiState(searchQuery, users, chatsMap)
    }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState()
        )

    fun updateSearchQuery(newValue: String) {
        searchQueryFlow.value = newValue
        viewModelScope.launch {
            val newQueryResult = userProfileService.getProfilesByQuery(newValue)
            usersFlow.value = newQueryResult
        }
    }

    fun clearSearchTextField() {
        searchQueryFlow.value = null
        usersFlow.value = listOf()
    }

}