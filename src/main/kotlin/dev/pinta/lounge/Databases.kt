package dev.pinta.lounge

import io.ktor.server.application.*
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases(): Connection {
    Class.forName("org.postgresql.Driver")
    val url = environment.config.property("postgres.url").getString()

    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()

    log.info("Connecting to postgres database at $url")
    return DriverManager.getConnection(url, user, password)
}
