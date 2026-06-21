package com.example.auth_service.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarToken(String correo) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiration); // Calcula la fecha de expiración sumando 24h (en milisegundos) a la fecha actual
        
        return Jwts.builder()
            .subject(correo)
            .issuedAt(ahora) // iat
            .expiration(expiracion) // exp
            .signWith(getSigningKey())      
            .compact();
    }

    public String extraerCorreo(String token) {
        if (token == null || token.isBlank()) return null;
        
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        try {
            return Jwts.parser() // Crea o devuelve un builder de parser JWT
                .verifyWith(getSigningKey()) // Configura validación usando la secret key
                .build() // Construye el parser final
                .parseSignedClaims(jwt) // Interpreta y valida el JWT
                .getPayload() // Obtiene payload
                .getSubject(); // Obtiene el claim "sub"
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean validarToken(String token) {
        if (token == null || token.isBlank()) return false;
        
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        try {
            Jwts.parser() // Crea builder parser JWT
                .verifyWith(getSigningKey()) // Configura validación de firma
                .build() // Construye parser final
                .parseSignedClaims(jwt); // Valida y parsea JWT
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}