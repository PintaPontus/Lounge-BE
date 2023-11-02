package com.pinta.lounge.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pinta.lounge.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class JWTUtils {

    @Value("${lounge.auth.key}")
    private String key;

    @Value("${lounge.auth.expiration}")
    private Long tokenExpiration;

    public String generateToken(UserEntity user) {
        return ("Bearer ").concat(generateToken(user.getId()));
    }

    public String generateToken(Long id) {
        return JWT.create()
            .withExpiresAt(
                LocalDateTime.now()
                    .plusSeconds(tokenExpiration)
                    .toInstant(ZoneOffset.UTC)
            )
            .withIssuer("Lounge")
            .withKeyId(id.toString())
            .sign(Algorithm.HMAC256(key));
    }

    public DecodedJWT decodeToken(String token){
        try {
            return JWT.require(Algorithm.HMAC256(key))
                .withIssuer("Lounge")
                .build()
                .verify(token);
        } catch (TokenExpiredException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public Long decodeId(String token){
        try{
            return Long.valueOf(this.decodeToken(token).getKeyId());
        }catch (NumberFormatException ex){
            return null;
        }
    }

}
