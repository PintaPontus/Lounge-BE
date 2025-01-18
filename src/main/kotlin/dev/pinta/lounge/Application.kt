package dev.pinta.lounge

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val db = configureDatabases()
    configureSecurity(db)
    configureRouting(db)
    configureSockets(db)
}
