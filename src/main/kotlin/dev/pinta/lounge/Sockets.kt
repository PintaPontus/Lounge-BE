package dev.pinta.lounge

import dev.pinta.lounge.dto.ChatMessage
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.sql.Connection
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets(db: Connection) {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        webSocket("/messages") { // websocketSession
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val message = receiveDeserialized<ChatMessage>()
//                    TODO: save message and send to others with db

//                    TODO: unauth close
//                    close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Client said BYE"))
                    outgoing.send(Frame.Text("(${call.parameters["id"]}) SAID: ${message.content}"))
                }
            }
        }
    }
}
