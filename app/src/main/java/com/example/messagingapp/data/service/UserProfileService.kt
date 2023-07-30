package com.example.messagingapp.data.service

import com.example.messagingapp.data.model.User
import com.example.messagingapp.utils.Constants.PHONE_NUMBER_FIELD
import com.example.messagingapp.utils.Constants.TAG_FIELD
import com.example.messagingapp.utils.Constants.USERS_COLL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserProfileService {

    private val usersColl = firestore.collection(USERS_COLL)

    override val currentUserFlow = callbackFlow {
        val listener = usersColl
            .document(auth.uid!!)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) cancel()
                else trySend(snapshot.toObject(User::class.java)!!)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun userExists(userId: String): Boolean {
        return usersColl
            .document(userId)
            .get()
            .await()
            .exists()
    }

    override suspend fun saveProfile(user: User) {
        usersColl.document(user.docId!!).set(user).await()
    }

    override suspend fun getProfilesByQuery(query: String): List<User> =
        if (query.length < 4) listOf() else
            usersColl
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

    override suspend fun getByPhoneNumbers(phoneNumbers: List<String>): List<User> =
        usersColl
            .whereIn(PHONE_NUMBER_FIELD, phoneNumbers)
            .get()
            .await()
            .toObjects(User::class.java)

    override suspend fun getProfileByDocumentId(docId: String): User? =
        usersColl
            .document(docId)
            .get()
            .await()
            .toObject(User::class.java)

}

interface UserProfileService {
    val currentUserFlow: Flow<User>
    suspend fun userExists(userId: String): Boolean
    suspend fun saveProfile(user: User)

    suspend fun getProfilesByQuery(query: String): List<User>
    suspend fun getByPhoneNumbers(phoneNumbers: List<String>): List<User>

    suspend fun getProfileByDocumentId(docId: String): User?
}