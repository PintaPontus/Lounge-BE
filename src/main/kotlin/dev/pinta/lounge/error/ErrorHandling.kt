package dev.pinta.lounge.error

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

fun Application.errorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
//            log
            cause.stackTrace.forEach { println(it.toString()) }
            when (cause) {
                is NotFoundException -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ExceptionResponse(cause)
                    )
                }

                is MissingRequestParameterException -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ExceptionResponse(cause)
                    )
                }

                is BadRequestException -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ExceptionResponse(cause)
                    )
                }

                // We can have other categories
                else -> {
                    // All the other Exceptions become status 500, with more info in development mode.
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ExceptionResponse(cause)
                    )
                }
            }
        }
    }
}

@Serializable
data class ExceptionResponse(
    val cause: String
) {
    constructor(cause: Throwable) : this(cause.message ?: cause.toString())
}