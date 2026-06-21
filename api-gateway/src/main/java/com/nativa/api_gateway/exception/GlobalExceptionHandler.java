package com.nativa.api_gateway.exception;

import java.time.Instant;

import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
 
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String message;
 
        // ── Token inválido o expirado lanzado por JwtUtil ─────────────────────
        if (ex instanceof UnauthorizedException) {
            status  = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage();
            log.warn("[401] {} — {}", exchange.getRequest().getPath(), message);
 
        // ── Errores de routing del gateway (404, 503, etc.) ───────────────────
        } else if (ex instanceof ResponseStatusException rse) {
            status  = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : status.getReasonPhrase();
            log.warn("[{}] {} — {}", status.value(), exchange.getRequest().getPath(), message);
 
        // ── Cualquier excepción no controlada ─────────────────────────────────
        } else {
            status  = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "Error interno del servidor";
            log.error("[500] {} — {}", exchange.getRequest().getPath(), ex.getMessage(), ex);
        }
 
        return writeResponse(exchange, status, message);
    }
 
    private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
 
        if (response.isCommitted()) {
            return Mono.error(new IllegalStateException("Response ya comprometida"));
        }
 
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
 
        String body = String.format(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\",\"timestamp\":\"%s\"}",
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getPath().value(),
                Instant.now()
        );
 
        var buffer = response.bufferFactory().wrap(body.getBytes());
        return response.writeWith(Mono.just(buffer));
    }
}
