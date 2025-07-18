package com.example.lifetrack.model.repository

import com.example.lifetrack.model.data.AuthResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun signUp(email: String, password: String, phoneNumber: String, displayName: String): AuthResult

}
