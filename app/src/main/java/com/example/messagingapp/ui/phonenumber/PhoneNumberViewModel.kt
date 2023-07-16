package com.example.messagingapp.ui.phonenumber

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.messagingapp.data.AuthenticationService
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class PhoneNumberViewModel @Inject constructor(
    authenticationService: AuthenticationService,
    @ActivityContext ctxt: Context
) :
    ViewModel() {

}