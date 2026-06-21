package com.nativa.api_gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TokenRelayFilter implements GatewayFilter{
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        log.info("[TokenRelayFilter] path: {} | Authorization header: {}", path,
                authHeader != null ? "PRESENTE" : "AUSENTE");

        if (authHeader == null) {
            log.info("[TokenRelayFilter] Sin Authorization header → sin relay");
            return chain.filter(exchange);
        }

        log.info("[TokenRelayFilter] Relaying Authorization header al microservicio downstream");
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
