package dev.pinta.lounge.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRTConnection(
    val authToken: String,
    val recipient: Long,
)

@Serializable
data class ChatMessageIn(
    val content: String,
)

@Serializable
data class ChatMessageOut(
    var sender: Long,
    val content: String,
)
