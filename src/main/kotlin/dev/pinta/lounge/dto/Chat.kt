package dev.pinta.lounge.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ChatRTConnection(
    val authToken: String,
    val recipient: Long,
)

@Serializable
data class ChatMessageIn(
    val content: String
)

@Serializable
data class ChatMessageOut(
    var sender: Long,
    val content: String,
    @Contextual
    val date: Instant,
)
