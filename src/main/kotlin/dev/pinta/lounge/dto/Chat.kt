package dev.pinta.lounge.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRTConnection(
    val authToken: String,
    val recipient: Int,
)

@Serializable
data class ChatMessage(
    var sender: Int,
    val content: String,
)
