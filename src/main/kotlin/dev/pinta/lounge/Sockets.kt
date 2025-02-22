package dev.pinta.lounge

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.SignatureVerificationException
import dev.pinta.lounge.dto.ChatMessage
import dev.pinta.lounge.dto.ChatRTConnection
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    val jwtIssuer = environment.config.property("lounge.name").getString()
    val jwtSecret = environment.config.property("lounge.security.secret").getString()
    val verifier: JWTVerifier = JWT.require(Algorithm.HMAC256(jwtSecret))
        .withIssuer(jwtIssuer)
        .build()

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

    routing {
        val broadcasts = Collections.synchronizedMap(mutableMapOf<Int, WebSocketServerSession>())

        webSocket("/messages") {
            val connection = receiveDeserialized<ChatRTConnection>()
            try {
                val senderId = verifier.verify(connection.authToken).subject.toInt()
                broadcasts[senderId] = this
                outgoing.send(Frame.Text("CONNECTED $senderId WITH TO ${connection.recipient}"))

                val recipient = connection.recipient

                while (broadcasts.containsKey(senderId)) {
                    val message = receiveDeserialized<ChatMessage>()
                    if (recipient != senderId) {
                        broadcasts[recipient]?.sendSerialized(message)
                    }
                }

                broadcasts.remove(senderId)
                close(CloseReason(CloseReason.Codes.GOING_AWAY, "Server said BYE"))
            } catch (_: SignatureVerificationException) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Unauthorized"))
            }
        }
    }
}
