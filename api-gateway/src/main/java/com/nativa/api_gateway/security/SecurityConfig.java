package com.nativa.api_gateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("[SecurityConfig] Configurando SecurityWebFilterChain...");
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, e) -> {
                            log.error("[SecurityConfig] authenticationEntryPoint disparado → path: {} | error: {}",
                                    exchange.getRequest().getPath(), e.getMessage());
                            var resp = exchange.getResponse();
                            resp.setStatusCode(HttpStatus.UNAUTHORIZED);
                            resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            var buf = resp.bufferFactory().wrap(
                                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Autenticación requerida\"}"
                                            .getBytes());
                            return resp.writeWith(Mono.just(buf));
                        })
                        .accessDeniedHandler((exchange, e) -> {
                            log.error("[SecurityConfig] accessDeniedHandler disparado → path: {} | error: {}",
                                    exchange.getRequest().getPath(), e.getMessage());
                            var resp = exchange.getResponse();
                            resp.setStatusCode(HttpStatus.FORBIDDEN);
                            resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            var buf = resp.bufferFactory().wrap(
                                    "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"Sin permiso para este recurso\"}"
                                            .getBytes());
                            return resp.writeWith(Mono.just(buf));
                        })
                )
                .authorizeExchange(exchanges -> {
                    log.info("[SecurityConfig] Registrando reglas de autorización...");
                    log.info("[SecurityConfig]   POST /api/auth/login  → permitAll");
                    log.info("[SecurityConfig]   POST /api/auth/register → permitAll");
                    log.info("[SecurityConfig]   anyExchange → authenticated");
                    exchanges
                            .pathMatchers(HttpMethod.POST, "api/auth/login").permitAll()
                            .pathMatchers(HttpMethod.POST, "api/auth/register").permitAll()
                            .anyExchange().authenticated();
                })
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
