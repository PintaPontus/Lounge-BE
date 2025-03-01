package dev.pinta.lounge

import dev.pinta.lounge.error.errorHandling
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabase()
    configureSecurity()
    configureRouting()
    errorHandling()
    configureSockets()
}
