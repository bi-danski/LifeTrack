package com.example.lifetrack.model.repository

import com.example.lifetrack.model.data.User
import com.example.lifetrack.model.data.AuthResult
import com.example.lifetrack.model.data.Practitioner
import com.example.lifetrack.model.data.Kiongozi
import com.google.firebase.firestore.ListenerRegistration

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun getUserById(userId: String): User?
    suspend fun updateUser(userId: String, user: User): AuthResult
    suspend fun deleteUser(userId: String): AuthResult
    suspend fun sendPasswordReset(email: String): AuthResult
    suspend fun logout()
    fun observeUser(userId: String, onChange: (User?) -> Unit): ListenerRegistration

    // Practitioner CRUD
    suspend fun getPractitioners(): List<Practitioner>
    suspend fun addPractitioner(practitioner: Practitioner): AuthResult
    suspend fun updatePractitioner(practitioner: Practitioner): AuthResult
    suspend fun deletePractitioner(practitioner: Practitioner): AuthResult

    // Admin (Kiongozi) CRUD
    suspend fun getAdmins(): List<Kiongozi>
    suspend fun addAdmin(admin: Kiongozi): AuthResult
    suspend fun updateAdmin(admin: Kiongozi): AuthResult
    suspend fun deleteAdmin(admin: Kiongozi): AuthResult

    // Patient CRUD
    suspend fun getPatients(): List<User>
    suspend fun addPatient(patient: User): AuthResult
    suspend fun updatePatient(patient: User): AuthResult
    suspend fun deletePatient(patient: User): AuthResult
}