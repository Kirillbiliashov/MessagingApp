package com.example.messagingapp.ui.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.User
import com.example.messagingapp.data.service.UserProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val searchQuery: String? = null,
    val users: List<User> = listOf()
)

@HiltViewModel
class ChatsScreenViewModel @Inject constructor(
    private val userProfileService: UserProfileService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun updateSearchQuery(newValue: String) {
        _uiState.update { it.copy(searchQuery = newValue) }
        viewModelScope.launch {
            val newQueryResult = userProfileService.getProfilesByQuery(newValue)
            _uiState.update { it.copy(users = newQueryResult) }
        }
    }

    fun clearSearchTextField() {
        _uiState.update { it.copy(searchQuery = null, users = listOf()) }
    }

}