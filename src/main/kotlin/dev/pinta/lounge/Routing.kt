package dev.pinta.lounge

import dev.pinta.lounge.auth.LoungePrincipal
import dev.pinta.lounge.repository.MessagesRepository
import dev.pinta.lounge.repository.UsersRepository
import dev.pinta.lounge.serialize.InstantSerializer
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.Instant

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json(Json {
            serializersModule = SerializersModule {
                contextual(Instant::class) { InstantSerializer }
            }
        })
    }
    install(CORS) {
        allowHost("0.0.0.0:8081")
        allowHost("localhost:4200")
        allowHeader(HttpHeaders.ContentType)
    }

    val usersRepository = UsersRepository()
    val messagesRepository = MessagesRepository()

    routing {
        staticResources("/", "static")
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {}

        get("/health") {
            call.respondText("Lounge has started!")
        }

        authenticate("auth-bearer") {
            get("/ping") {
                val userId = call.principal<LoungePrincipal>()!!.id
                val user = usersRepository.findById(userId)
                    ?: throw NotFoundException("User with id $userId not found")

                call.respondText("Hello, ${user.username}!")
            }

            get("/last-direct-messages") {
                val id = call.principal<LoungePrincipal>()!!.id
                val page = call.request.queryParameters["page"]?.toLong() ?: 0
                val size = call.request.queryParameters["size"]?.toInt() ?: 20

                call.respond(messagesRepository.findByUserPaged(id, page, size))
            }

//            TODO: implement group chats
//            get("/last-group-messages") {
//                call.request.queryParameters["page"]
//                call.request.queryParameters["size"]
//                call.request.queryParameters["chat"]
//
//                call.respond()
//            }
        }

    }
}
