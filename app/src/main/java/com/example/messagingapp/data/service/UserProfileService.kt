package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.User
import com.google.firebase.firestore.Filter
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

    override suspend fun getProfilesByQuery(query: String): List<User> =
        if (query.length < 4) listOf() else
            firestore
                .collection("users")
                .orderBy("tag")
                .where(
                    Filter.or(
                        Filter.equalTo("phoneNumber", query),
                        Filter.and(
                            Filter.greaterThanOrEqualTo("tag", query),
                            Filter.lessThanOrEqualTo("tag", "$query\uf8ff")
                        )

                    )
                )
                .get()
                .await()
                .toObjects(User::class.java)

    override suspend fun getProfileByDocumentId(docId: String): User? = firestore
        .collection("users")
        .document(docId)
        .get()
        .await()
        .toObject(User::class.java)

}

interface UserProfileService {
    suspend fun userExists(userId: String): Boolean
    suspend fun saveProfile(user: User)

    suspend fun getProfilesByQuery(query: String): List<User>

    suspend fun getProfileByDocumentId(docId: String): User?
}