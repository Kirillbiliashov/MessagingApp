package com.example.messagingapp.ui.addProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.service.AuthenticationService
import com.example.messagingapp.data.service.UserProfileService
import com.example.messagingapp.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProfileViewModel @Inject constructor(
    private val authService: AuthenticationService,
    private val userProfileService: UserProfileService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun updateFirstNameTextFieldValue(newValue: String) {
        _uiState.update { it.copy(firstName = newValue) }
    }

    fun updateLastNameTextFieldValue(newValue: String) {
        _uiState.update { it.copy(lastName = newValue) }
    }

    fun updateDescriptionTextFieldValue(newValue: String) {
        _uiState.update { it.copy(description = newValue) }
    }

    fun updateTagTextFieldValue(newValue: String) {
        _uiState.update { it.copy(tag = newValue) }
    }


    fun saveUserProfile() {
        val authUser = authService.currentUser!!
        val user = _uiState.value.toUser()
        viewModelScope.launch {
            userProfileService.saveProfile(
                user.copy(
                    docId = authUser.uid,
                    phoneNumber = authUser.phoneNumber
                )
            )
        }
    }

}

data class UiState(
    val firstName: String = "",
    val lastName: String = "",
    val description: String = "",
    val tag: String = ""
)

fun UiState.toUser() = User(
    firstName = firstName,
    lastName = lastName,
    description = description,
    tag = tag
)