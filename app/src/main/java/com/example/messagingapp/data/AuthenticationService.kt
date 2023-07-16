package com.example.messagingapp.data

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthenticationServiceImpl @Inject constructor(auth: FirebaseAuth) : AuthenticationService

interface AuthenticationService