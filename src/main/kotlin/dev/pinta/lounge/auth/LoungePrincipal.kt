package dev.pinta.lounge.auth

data class LoungePrincipal(
    val id: Long,
    val username: String,
)

enum class JWTClaims(val key: String) {
    ID("id"),
    USER_NAME("username"),
}