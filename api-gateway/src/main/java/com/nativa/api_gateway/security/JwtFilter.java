package com.nativa.api_gateway.security;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.nativa.api_gateway.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter{

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().name();
        log.info("[JwtFilter] >>> Petición entrante: {} {}", method, path);

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        log.info("[JwtFilter] Authorization header: {}", authHeader != null ? authHeader.substring(0, Math.min(authHeader.length(), 20)) + "..." : "AUSENTE");

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.info("[JwtFilter] Sin token Bearer → pasando al siguiente filtro (ruta pública o falta token)");
            return chain.filter(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        log.info("[JwtFilter] Token extraído (primeros 20 chars): {}...", token.substring(0, Math.min(token.length(), 20)));

        try {
            log.info("[JwtFilter] Validando token...");
            jwtUtil.validateToken(token);
            log.info("[JwtFilter] Token válido ✓");
        } catch (UnauthorizedException e) {
            log.error("[JwtFilter] Token inválido: {}", e.getMessage());
            return onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("[JwtFilter] Error inesperado al validar token: {} - {}", e.getClass().getName(), e.getMessage());
            return onError(exchange, "Error interno al validar token", HttpStatus.UNAUTHORIZED);
        }

        UsernamePasswordAuthenticationToken authentication = buildAuthentication(token);
        log.info("[JwtFilter] Autenticación construida para usuario: {}", authentication.getName());

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(String token) {
        log.info("[JwtFilter] Extrayendo username del token...");
        String username = jwtUtil.extractUsername(token);
        log.info("[JwtFilter] Username extraído: {}", username);
        return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        log.error("[JwtFilter] Respondiendo con error {}: {}", status.value(), message);
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message
        );
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}
