package com.example.messagingapp.di

import com.example.messagingapp.data.service.AuthenticationService
import com.example.messagingapp.data.service.AuthenticationServiceImpl
import com.example.messagingapp.data.service.ChatService
import com.example.messagingapp.data.service.ChatServiceImpl
import com.example.messagingapp.data.service.MessageService
import com.example.messagingapp.data.service.MessageServiceImpl
import com.example.messagingapp.data.service.UserProfileService
import com.example.messagingapp.data.service.UserProfileServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    @Singleton
    abstract fun bindAuthService(
        authService: AuthenticationServiceImpl
    ): AuthenticationService

    @Binds
    @Singleton
    abstract fun bindUserProfileService(
        userProfileService: UserProfileServiceImpl
    ): UserProfileService

    @Binds
    @Singleton
    abstract fun bindChatService(
        chatService: ChatServiceImpl
    ): ChatService

    @Binds
    @Singleton
    abstract fun bindMessageService(
        messageService: MessageServiceImpl
    ): MessageService
}


@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

}
