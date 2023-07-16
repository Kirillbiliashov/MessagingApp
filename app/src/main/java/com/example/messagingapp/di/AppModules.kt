package com.example.messagingapp.di

import com.example.messagingapp.data.AuthenticationService
import com.example.messagingapp.data.AuthenticationServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    abstract fun bindAuthService(
        authenticationService: AuthenticationServiceImpl
    ): AuthenticationService

}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

}
