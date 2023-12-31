package com.example.messagingapp.ui.addGroupChat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.model.Chat
import com.example.messagingapp.data.model.GroupInfo
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
    val name: String = "",
    val tag: String = "",
    val isPrivate: Boolean = false,
    val dialogShown: Boolean = false,
    val membersQuery: String = "",
    val groupChatMembers: List<User> = listOf(),
    val selectedMembers: List<User> = listOf(),
    val queryUsers: List<User> = listOf()
)

@HiltViewModel
class AddGroupChatScreenViewModel @Inject constructor(
    private val userProfileService: UserProfileService,
    private val chatService: ChatService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun updateNameTextField(newValue: String) {
        _uiState.update { it.copy(name = newValue) }
    }

    fun updateTagTextField(newValue: String) {
        _uiState.update { it.copy(tag = newValue) }
    }

    fun displayFindMemberDialog() {
        _uiState.update { it.copy(dialogShown = true) }
    }

    fun updateIsPrivateValue(newValue: Boolean) {
        _uiState.update { it.copy(isPrivate = newValue) }
    }

    fun updateMembersQueryTextField(newValue: String) {
        _uiState.update { it.copy(membersQuery = newValue) }
        viewModelScope.launch {
            val users = userProfileService.getProfilesByQuery(newValue)
            _uiState.update { it.copy(queryUsers = users) }
        }
    }

    fun updateSelectedMembers(member: User, selected: Boolean) {
        val members = _uiState.value.selectedMembers
        if (selected) {
            _uiState.update { it.copy(selectedMembers = members + member) }
        } else {
            _uiState.update { it.copy(selectedMembers = members - member) }
        }
    }

    fun closeDialogWindow() {
        _uiState.update { it.copy(dialogShown = false) }
    }

    fun addSelectedMembers() {
        val selectedMembers = _uiState.value.selectedMembers
        val groupChatMembers = _uiState.value.groupChatMembers
        _uiState.update {
            it.copy(
                groupChatMembers = groupChatMembers + selectedMembers,
                dialogShown = false
            )
        }
    }

    fun removeSelectedMember(user: User) {
        val groupChatMembers = _uiState.value.groupChatMembers
        val selectedMembers = _uiState.value.selectedMembers
        _uiState.update {
            it.copy(
                groupChatMembers = groupChatMembers - user,
                selectedMembers = selectedMembers - user
            )
        }
    }

    fun createNewChatGroup() {
        val groupChat = Chat(
            isGroup = true,
            groupInfo = GroupInfo(
                name = _uiState.value.name,
                tag = _uiState.value.tag,
                isPrivate = _uiState.value.isPrivate,
                members = _uiState.value.groupChatMembers.map { it.phoneNumber!! }
            )
        )
        viewModelScope.launch {
            chatService.createChatGroup(groupChat)
        }
    }

}