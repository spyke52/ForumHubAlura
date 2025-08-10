package com.alura.forum.forumHub.infra.security;

import com.alura.forum.forumHub.domain.usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generarToken(Usuario usuario) {
        try {
            var algoritmo = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
            return JWT.create()
                    .withIssuer("API ForoHub")
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(fechaExpiracion())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error al generar el token JWT", exception);
        }
    }

    public String getSubject(String token) {
        try {
            var algoritmo = Algorithm.HMAC256(secret.getBytes(StandardCharsets.UTF_8));
            JWTVerifier verifier = JWT.require(algoritmo)
                    .withIssuer("API ForoHub")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido: " + exception.getMessage());
        }
    }

    private Instant fechaExpiracion() {
        // Zona horaria de Argentina (UTC-3)
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.ofHours(-3));
    }
}