package com.example.messagingapp.data.service

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationServiceImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthenticationService {

    private lateinit var verificationId: String
    override val isCodeSentFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val userMessageFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    override val currentUser
        get() = auth.currentUser

    override suspend fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun authenticateWithVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        auth.signInWithCredential(credential).await()
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            auth.signInWithCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                userMessageFlow.value = "Invalid phone number. Enter valid phone number."
            } else if (e is FirebaseTooManyRequestsException) {
                userMessageFlow.value = "Too many attempts. Try again in a few minutes."
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            isCodeSentFlow.value = true
            userMessageFlow.value = "The code is sent."
            this@AuthenticationServiceImpl.verificationId = verificationId
        }
    }

}

interface AuthenticationService {
    val isCodeSentFlow: MutableStateFlow<Boolean>
    val userMessageFlow: MutableStateFlow<String?>
    val currentUser: FirebaseUser?
    suspend fun sendVerificationCode(phoneNumber: String, activity: Activity)
    suspend fun authenticateWithVerificationCode(code: String)
}