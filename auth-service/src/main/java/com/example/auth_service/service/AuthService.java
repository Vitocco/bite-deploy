package com.example.auth_service.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auth_service.model.Credencial;
import com.example.auth_service.repository.CredencialRepository;
import com.example.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final CredencialRepository credencialRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
@Transactional
public String login(String correo, String password) {
    Optional<Credencial> credencialOpt = credencialRepository.findByCorreoUser(correo);
    
    if (credencialOpt.isEmpty()) {
        log.warn("Login fallido - usuario no existe: {}", correo);
        return null;
    }
    Credencial credencial = credencialOpt.get();
    if (!credencial.getActivo()) {
        log.warn("Login fallido - usuario inactivo: {}", correo);
        return null;
    }
    if (!passwordEncoder.matches(password, credencial.getContrasenaUser())) {
        log.warn("Login fallido - password incorrecto: {}", correo);
        return null;
    }
    log.info("Login exitoso para: {}", correo);
    return jwtUtil.generarToken(correo);
}

@Transactional
public String register(String correo, String password) {
    Optional<Credencial> existente = credencialRepository.findByCorreoUser(correo);
    
    if (correo == null || !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
        log.warn("Register fallido - correo inválido: {}", correo);
        return "Correo inválido";
    }
    if (existente.isPresent()) {
        log.warn("Register fallido - usuario ya existe: {}", correo);
        return "El usuario ya existe";
    }
    Credencial nueva = new Credencial();
    nueva.setCorreoUser(correo);
    nueva.setContrasenaUser(passwordEncoder.encode(password));
    nueva.setActivo(true);
    credencialRepository.save(nueva);
    log.info("Usuario registrado exitosamente: {}", correo);
    return "Usuario creado exitosamente";
    }

}