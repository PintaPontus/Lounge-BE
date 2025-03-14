package dev.pinta.lounge

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.SignatureVerificationException
import dev.pinta.lounge.auth.JWTClaims
import dev.pinta.lounge.dto.ChatMessageIn
import dev.pinta.lounge.dto.ChatMessageOut
import dev.pinta.lounge.dto.ChatRTConnection
import dev.pinta.lounge.repository.Message
import dev.pinta.lounge.repository.MessagesRepository
import dev.pinta.lounge.serialize.InstantSerializer
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
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
                serializersModule = SerializersModule {
                    contextual(Instant::class) { InstantSerializer }
                }
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

            val senderId = try {
//                AUTH
                verifier.verify(connection.authToken)
                    .getClaim(JWTClaims.ID.key)
                    .asLong()
            } catch (e: SignatureVerificationException) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Unauthorized"))
                return@webSocket
            }

//            SETUP
            broadcasts[senderId] = this
            sendSerialized(
                messagesRepository.findByUserPaged(senderId, connection.recipient, 0, 20)
                    .map {
                        ChatMessageOut(
                            it.sender,
                            it.content,
                            it.date,
                        )
                    })

            try {
//                LOOP
                while (broadcasts.containsKey(senderId)) {
                    val message = receiveDeserialized<ChatMessageIn>()

                    val timestamp = Instant.now()

//                    PERSISTENCE
                    messagesRepository.create(
                        Message(
                            0,
                            senderId,
                            connection.recipient,
                            message.content,
                            timestamp,
                        )
                    )

//                    ECHO
                    if (connection.recipient != senderId) {
                        broadcasts[connection.recipient]?.sendSerialized(
                            listOf(
                                ChatMessageOut(
                                senderId,
                                message.content,
                                timestamp
                                )
                            )
                        )
                    }
                }

//                CLOSING
                broadcasts.remove(senderId)
                close(CloseReason(CloseReason.Codes.GOING_AWAY, "Server said BYE"))
            } catch (_: ClosedReceiveChannelException) {
                broadcasts.remove(senderId)
            }
        }
    }
}
