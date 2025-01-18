package dev.pinta.lounge.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val auth: String,
    val content: String,
    val recipient: String
)
