package dev.pinta.lounge

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import dev.pinta.lounge.auth.JWTClaims
import dev.pinta.lounge.auth.LoungePrincipal
import dev.pinta.lounge.dto.AuthRequest
import dev.pinta.lounge.dto.AuthResponse
import dev.pinta.lounge.repository.UsersRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.mindrot.jbcrypt.BCrypt

fun Application.configureSecurity() {
    val jwtIssuer = environment.config.property("lounge.name").getString()
    val jwtSecret = environment.config.property("lounge.security.secret").getString()
    val verifier: JWTVerifier = JWT.require(Algorithm.HMAC256(jwtSecret))
        .withIssuer(jwtIssuer)
        .build()

    authentication {
        bearer("auth-bearer") {
            authenticate { creds ->
                try {
                    val decodedJWT = verifier.verify(creds.token)
                    LoungePrincipal(
                        decodedJWT.getClaim(JWTClaims.ID.key)
                            .asLong(),
                        decodedJWT.getClaim(JWTClaims.USER_NAME.key)
                            .asString()
                    )
                } catch (_: JWTVerificationException) {
                }
            }
        }
    }

    val userRepository = UsersRepository()

    routing {
        post("/login") {
            val info = call.receive<AuthRequest>()
            log.info(info.toString())

            val user = userRepository.findByUsername(info.username)
            log.info(encryptPassword(info.password))

            if (user == null || (!verifyPassword(info.password, user.password))) {
                log.error("UNAUTHORIZED")
                call.respond(HttpStatusCode.Unauthorized)
            } else {
                val token = JWT.create()
                    .withIssuer(jwtIssuer)
                    .withClaim("id", user.id)
                    .withClaim("username", user.username)
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
