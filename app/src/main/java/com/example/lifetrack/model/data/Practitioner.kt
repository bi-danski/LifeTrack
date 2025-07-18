package com.example.lifetrack.model.data

data class Practitioner(
    var uuid: String = java.util.UUID.randomUUID().toString(),
    val accessLevel: Int = 0,
    val hospitalId: String,
    val lifetrackId: String,
    val fullName: String = "",
    val phoneNumber: String = "",
    val emailAddress: String = "",
    val password: String = "",
    val role: String = "practitioner",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)