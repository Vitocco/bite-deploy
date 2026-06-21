package com.nativa.api_gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nativa.api_gateway.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey() {
        log.debug("[JwtUtil] Generando signing key (longitud secret: {} chars)", secret != null ? secret.length() : "NULL");
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {
        log.info("[JwtUtil] Extrayendo claims del token...");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            log.info("[JwtUtil] Claims extraídos → subject: {}, exp: {}", claims.getSubject(), claims.getExpiration());
            return claims;
        } catch (Exception e) {
            log.error("[JwtUtil] Error extrayendo claims: {} - {}", e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    public String extractUsername(String token) {
        String username = extractAllClaims(token).getSubject();
        log.info("[JwtUtil] Username extraído: {}", username);
        return username;
    }

    public boolean isTokenExpired(String token) {
        boolean expired = extractAllClaims(token).getExpiration().before(new Date());
        log.info("[JwtUtil] ¿Token expirado? {}", expired);
        return expired;
    }

    public boolean validateToken(String token) {
        log.info("[JwtUtil] Iniciando validación de token...");
        try {
            boolean valid = !isTokenExpired(token);
            log.info("[JwtUtil] Token válido: {}", valid);
            return valid;
        } catch (ExpiredJwtException e) {
            log.error("[JwtUtil] Token EXPIRADO: {}", e.getMessage());
            throw new UnauthorizedException("Token expirado");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("[JwtUtil] Token INVÁLIDO o malformado: {} - {}", e.getClass().getName(), e.getMessage());
            throw new UnauthorizedException("Token inválido o malformado");
        }
    }
}
