package dev.pinta.lounge.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val userId: Int,
    val token: String
)