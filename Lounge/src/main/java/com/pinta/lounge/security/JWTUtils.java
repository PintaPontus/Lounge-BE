package com.pinta.lounge.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;

@Component
public class JWTUtils {

    @Value("${lounge.auth.key}")
    private String key;

    @Value("${lounge.auth.expiration}")
    private Long tokenExpiration;

    public String generateToken(Long id){
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
        return JWT.require(Algorithm.HMAC256(key))
            .withIssuer("Lounge")
            .build()
            .verify(token);
    }

    public Long decodeId(String token){
        try{
            return Long.valueOf(this.decodeToken(token).getKeyId());
        }catch (NumberFormatException ex){
            return null;
        }
    }

}
