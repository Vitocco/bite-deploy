package com.example.auth_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth_service.model.Credencial;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {
    Optional<Credencial> findByCorreoUser(String correoUser); 

    /** 
     * Optional funciona como una "Caja". 
     * Obliga al desarrollador a manejar explícitamente la posibilidad de que 
     * el objeto buscado no exista en la base de datos, evitando así el 
     * famoso NullPointerException que provoca crasheos en las aplicaciones Java.
     */

}
