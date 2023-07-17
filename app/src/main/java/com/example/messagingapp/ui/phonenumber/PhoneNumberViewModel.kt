package com.example.messagingapp.ui.phonenumber

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.service.AuthenticationService
import com.example.messagingapp.data.service.UserProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class UiState(
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val codeSent: Boolean = false,
    val userMessage: String? = null
)

@HiltViewModel
class PhoneNumberViewModel @Inject constructor(
    private val authService: AuthenticationService,
    private val userProfileService: UserProfileService
) : ViewModel() {

    private val phoneNumberFlow = MutableStateFlow("")
    private val verificationCodeFlow = MutableStateFlow("")

    val uiState =
        combine(
            phoneNumberFlow, verificationCodeFlow,
            authService.isCodeSentFlow,
            authService.userMessageFlow
        ) { number, code, codeSent, userMessage ->
            UiState(
                phoneNumber = number, verificationCode = code,
                codeSent = codeSent, userMessage = userMessage
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState()
        )


    fun changePhoneNumberValue(newValue: String) {
        phoneNumberFlow.value = newValue
    }

    fun changeVerificationCodeValue(newValue: String) {
        verificationCodeFlow.value = newValue
    }

    fun sendVerificationCode(activity: Activity) {
        viewModelScope.launch {
            authService.sendVerificationCode(phoneNumberFlow.value, activity)
        }
    }

    fun verifyCode(completionHandler: (Boolean) -> Unit) {
        viewModelScope.launch {
            authService.authenticateWithVerificationCode(verificationCodeFlow.value)
            val userId = authService.currentUser?.uid
            println("user id: $userId")
            userId?.let { id ->
                completionHandler(userProfileService.userExists(id))
            }
        }
    }
}