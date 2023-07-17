package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserProfileService {
    override suspend fun userExists(userId: String): Boolean {
        return firestore
            .collection("users")
            .document(userId)
            .get()
            .await()
            .exists()
    }

    override suspend fun saveProfile(user: User) {
        firestore.collection("users").document(user.docId!!).set(user).await()
    }

}

interface UserProfileService {
    suspend fun userExists(userId: String): Boolean
    suspend fun saveProfile(user: User)
}