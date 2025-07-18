package com.example.lifetrack.model.data

data class User(
    var uuid: String = "",
    val lifetrackId: String?,
    val emailAddress: String,
    val phoneNumber: String,
    val password: String = "",
    val fullName: String = "",
    val profileImageUrl: String = "",
    val role: String = "patient",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)











