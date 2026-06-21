package com.example.auth_service.security;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component("jwtFilter")
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,   // Petición HTTP entrante
            HttpServletResponse response, // Respuesta HTTP saliente
            FilterChain filterChain)      // Cadena de filtros siguiente
            throws ServletException, IOException {
        
        // Obtiene el header "Authorization" de la petición HTTP
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {  
            String token = authHeader.substring(7);

            if (jwtUtil.validarToken(token)) {
                String correo = jwtUtil.extraerCorreo(token);
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(
                        correo, null, new ArrayList<>()
                    );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }      
        filterChain.doFilter(request, response);
    }
}

