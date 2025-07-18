package com.example.lifetrack.model.repository

import com.example.lifetrack.model.data.AuthResult
import com.example.lifetrack.model.data.Practitioner
import com.example.lifetrack.model.data.User
import com.example.lifetrack.model.data.Kiongozi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await


class UserRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {

    override suspend fun getCurrentUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return getUserById(userId)
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = firestore.collection("Patients").document(userId).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUser(userId: String, user: User): AuthResult {
        return try {
            val updatedUser = user.copy(updatedAt = System.currentTimeMillis())
            firestore.collection("Patients").document(userId).set(updatedUser).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to update user")
        }
    }

    override suspend fun deleteUser(userId: String): AuthResult {
        return try {
            firestore.collection("Patients").document(userId).delete().await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to delete user")
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun sendPasswordReset(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to send reset email")
        }
    }

    override fun observeUser(userId: String, onChange: (User?) -> Unit): ListenerRegistration {
        return firestore.collection("Patients")
            .document(userId)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject(User::class.java)
                onChange(user)
            }
    }
    override suspend fun getPractitioners(): List<Practitioner> {
        try {
            val snapshot = firestore.collection("Practitioners").get().await()
            return snapshot.documents.mapNotNull { it.toObject(Practitioner::class.java) }
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    override suspend fun addPractitioner(practitioner: Practitioner): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(practitioner.emailAddress, practitioner.password).result
            if (result == null || result.user == null) {
                return AuthResult.Failure("FirebaseAuth error on creating user")
            }
            practitioner.uuid = result.user?.uid ?: java.util.UUID.randomUUID().toString()
            firestore.collection("Practitioners")
                .document(practitioner.uuid.toString())
                .set(practitioner)
                .await()
            AuthResult.SuccessWithData(practitioner)
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to add practitioner")
        }
    }

    override suspend fun updatePractitioner(practitioner: Practitioner): AuthResult {
        return try {
            firestore.collection("Practitioners")
                .document(practitioner.uuid.toString())
                .set(practitioner)
                .await()
            AuthResult.SuccessWithData(practitioner)
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to update practitioner")
        }
    }

    override suspend fun deletePractitioner(practitioner: Practitioner): AuthResult {
        return try {
            firestore.collection("Practitioners")
                .document(practitioner.uuid.toString())
                .delete()
                .await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to delete practitioner")
        }
    }

    override suspend fun getAdmins(): List<Kiongozi> {
        return try {
            val snapshot = firestore.collection("Kiongos").get().await()
            snapshot.documents.mapNotNull { it.toObject(Kiongozi::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addAdmin(admin: Kiongozi): AuthResult {
        return try {
            firestore.collection("Kiongos")
                .document(admin.id)
                .set(admin)
                .await()
            AuthResult.SuccessWithData(admin)
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to add admin")
        }
    }

    override suspend fun updateAdmin(admin: Kiongozi): AuthResult {
        return try {
            firestore.collection("Kiongos")
                .document(admin.id)
                .set(admin)
                .await()
            AuthResult.SuccessWithData(admin)
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to update admin")
        }
    }

    override suspend fun deleteAdmin(admin: Kiongozi): AuthResult {
        return try {
            firestore.collection("Kiongos")
                .document(admin.id)
                .delete()
                .await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to delete admin")
        }
    }

    override suspend fun getPatients(): List<User> {
        return try {
            val snapshot = firestore.collection("Patients").get().await()
            snapshot.documents.mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addPatient(patient: User): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(patient.emailAddress, patient.password).result
            if (result == null || result.user == null) {
                return AuthResult.Failure("Failed to create user")
            }
            patient.uuid = result.user?.uid ?: java.util.UUID.randomUUID().toString()
//            patient.lifetrackId =
            firestore.collection("Patients")
                .document(patient.uuid)
                .set(patient)
                .await()
            AuthResult.SuccessWithData(patient)
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to add patient")
        }
    }

    override suspend fun updatePatient(patient: User): AuthResult {
        return try {
            firestore.collection("Patients")
                .document(patient.uuid.toString())
                .set(patient)
                .await()
            AuthResult.SuccessWithData(patient)
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to update patient")
        }
    }

    override suspend fun deletePatient(patient: User): AuthResult {
        return try {
            firestore.collection("Patients")
                .document(patient.uuid.toString())
                .delete()
                .await()
            AuthResult.Success
        } catch (e: Exception) {
            AuthResult.Failure(e.message ?: "Failed to delete patient")
        }
    }
}