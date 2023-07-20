package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.firebase.User
import com.example.messagingapp.utils.Constants
import com.example.messagingapp.utils.Constants.PHONE_NUMBER_FIELD
import com.example.messagingapp.utils.Constants.TAG_FIELD
import com.example.messagingapp.utils.Constants.USERS_COLL
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
            .collection(USERS_COLL)
            .document(userId)
            .get()
            .await()
            .exists()
    }

    override suspend fun saveProfile(user: User) {
        firestore.collection(USERS_COLL).document(user.docId!!).set(user).await()
    }

    override suspend fun getProfilesByQuery(query: String): List<User> =
        if (query.length < 4) listOf() else
            firestore
                .collection(USERS_COLL)
                .orderBy(TAG_FIELD)
                .where(
                    Filter.or(
                        Filter.equalTo(PHONE_NUMBER_FIELD, query),
                        Filter.and(
                            Filter.greaterThanOrEqualTo(TAG_FIELD, query),
                            Filter.lessThanOrEqualTo(TAG_FIELD, "$query\uf8ff")
                        )
                    )
                )
                .get()
                .await()
                .toObjects(User::class.java)

    override suspend fun getProfileByDocumentId(docId: String): User? = firestore
        .collection(USERS_COLL)
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