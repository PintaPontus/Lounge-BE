package dev.pinta.lounge

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.SignatureVerificationException
import dev.pinta.lounge.dto.ChatMessageIn
import dev.pinta.lounge.dto.ChatMessageOut
import dev.pinta.lounge.dto.ChatRTConnection
import dev.pinta.lounge.repository.Message
import dev.pinta.lounge.repository.MessagesRepository
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }

    val jwtIssuer = environment.config.property("lounge.name")
        .getString()
    val jwtSecret = environment.config.property("lounge.security.secret")
        .getString()
    val verifier: JWTVerifier = JWT.require(Algorithm.HMAC256(jwtSecret))
        .withIssuer(jwtIssuer)
        .build()

    val messagesRepository = MessagesRepository()

    routing {
        val broadcasts = Collections.synchronizedMap(mutableMapOf<Long, WebSocketServerSession>())

        webSocket("/messages") {
//            START
            val connection = receiveDeserialized<ChatRTConnection>()

            try {
//                AUTH
                val senderId = verifier.verify(connection.authToken).subject.toLong()

//                SETUP
                broadcasts[senderId] = this
                outgoing.send(Frame.Text("CONNECTED $senderId WITH TO ${connection.recipient}"))

//                LOOP
                while (broadcasts.containsKey(senderId)) {
                    val message = receiveDeserialized<ChatMessageIn>()

//                    PERSISTENCE
                    messagesRepository.create(
                        Message(
                            0,
                            senderId,
                            connection.recipient,
                            message.content,
                            Instant.now(),
                        )
                    )

//                    ECHO
                    if (connection.recipient != senderId) {
                        broadcasts[connection.recipient]?.sendSerialized(ChatMessageOut(senderId, message.content))
                    }
                }

//                CLOSING
                broadcasts.remove(senderId)
                close(CloseReason(CloseReason.Codes.GOING_AWAY, "Server said BYE"))
            } catch (_: SignatureVerificationException) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Unauthorized"))
            }
        }
    }
}
