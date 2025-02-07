package dev.pinta.lounge

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import dev.pinta.lounge.dto.AuthRequest
import dev.pinta.lounge.dto.AuthResponse
import dev.pinta.lounge.repository.UsersService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt
import java.sql.Connection


fun Application.configureSecurity(dbConnection: Connection) {
    val jwtIssuer = environment.config.property("lounge.name").getString()
    val jwtSecret = environment.config.property("lounge.security.secret").getString()
    val verifier: JWTVerifier = JWT.require(Algorithm.HMAC256(jwtSecret))
        .withIssuer(jwtIssuer)
        .build()

    val userService = UsersService(dbConnection)

    authentication {
        bearer("auth-bearer") {
            authenticate { creds ->
                try {
                    val decodedJWT = verifier.verify(creds.token)
                    UserIdPrincipal(decodedJWT.subject)
                } catch (_: JWTVerificationException) {
                }
            }
        }
    }
    routing {
        post("/login") {
            val info = call.receive<AuthRequest>()
            log.info(info.toString())

            val user = userService.findByUsername(info.username)
            log.info(encryptPassword(info.password))

            if (user == null || (!verifyPassword(info.password, user.password))) {
                log.error("UNAUTHORIZED")
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val token = JWT.create()
                    .withIssuer(jwtIssuer)
                    .withSubject(user.id.toString())
                    .sign(Algorithm.HMAC256(jwtSecret))
                log.info("ACCESS COMPLETE")
                call.respond(AuthResponse(user.id, token))
            }

        }
    }
}

fun encryptPassword(password: String): String? {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun verifyPassword(plainPassword: String, hashPassword: String): Boolean {
    return BCrypt.checkpw(plainPassword, hashPassword)
}
