package dev.pinta.lounge

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    val url = environment.config.property("postgres.url").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()

    log.info("Connecting to postgres database at $url")

    Database.connect(
        url,
        user = user,
        password = password
    )
}
