package com.example.lifetrack.model.repository

import com.example.lifetrack.model.data.AuthResult
import com.example.lifetrack.model.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Login failed")
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        phoneNumber: String,
        displayName: String
    ): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            val userId = firebaseUser?.uid ?: throw IllegalStateException("User ID is null")
            val lifetrackID = generateLifeTrackID()
            result.user.hashCode()
            val user = User(
                emailAddress = email,
                phoneNumber = phoneNumber,
                lifetrackId = lifetrackID,
                fullName = displayName,
                uuid = userId
            )
            firestore.collection("Patients").document(userId).set(user).await()
            AuthResult.Success
        } catch (e: FirebaseAuthWeakPasswordException) {
            AuthResult.Failure("Password is too weak. Please use a stronger password.")
        } catch (e: FirebaseAuthUserCollisionException) {
            AuthResult.Failure("An account with this email already exists.")
        } catch (e: FirebaseFirestoreException) {
            AuthResult.Failure("Firebase Firestore Error!! ${e.message}")
        } catch (e: Exception) {
        AuthResult.Failure("Technical Registration Failure!! ${e.message}, Please try again later.")
    }
    }

    private fun generateLifeTrackID(): String {
//        val combinedString = "$email:$fUUID"
        val uuid = java.util.UUID.randomUUID()
//        val hash = java.security.MessageDigest
//            .getInstance("SHA-256")
//            .digest(combinedString.toByteArray())
//        val uuid  = hash.fold(0L) { acc, byte -> (acc * 31 + byte.toUByte().toLong()) % 1_000_000_000_000L }
        return  "LT_" + uuid.toString().padStart(12, '0')
    }
}