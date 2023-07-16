package com.example.messagingapp.ui.phonenumber

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagingapp.data.AuthenticationService
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
    val codeSent: Boolean = false
)

@HiltViewModel
class PhoneNumberViewModel @Inject constructor(
    private val authService: AuthenticationService
) : ViewModel() {

    private val phoneNumberFlow = MutableStateFlow("")
    private val verificationCodeFlow = MutableStateFlow("")

    val uiState =
        combine(
            phoneNumberFlow, verificationCodeFlow,
            authService.isCodeSentFlow
        ) { number, code, codeSent ->
            UiState(phoneNumber = number, verificationCode = code, codeSent = codeSent)
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

    fun verifyCode() {
        viewModelScope.launch {
            authService.authenticateWithVerificationCode(verificationCodeFlow.value)
        }
    }

}