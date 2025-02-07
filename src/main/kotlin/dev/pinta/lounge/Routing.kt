package dev.pinta.lounge

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection

fun Application.configureRouting(db: Connection) {
//    val userService = UsersService(db)

    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        allowHost("0.0.0.0:8081")
        allowHost("localhost:4200")
        allowHeader(HttpHeaders.ContentType)
    }
    routing {
        staticResources("/", "static")
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {}

        authenticate("auth-bearer") {
            get("/hello") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }

    }
}
