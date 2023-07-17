package com.example.messagingapp.ui.start

import androidx.lifecycle.ViewModel
import com.example.messagingapp.data.service.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartScreenViewModel @Inject constructor(
    private val authService: AuthenticationService
) : ViewModel() {

    val moveToChatsScreen = authService.isUserAuthenticated
}