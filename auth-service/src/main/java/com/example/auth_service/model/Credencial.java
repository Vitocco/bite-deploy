package com.example.auth_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "credencial")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Credencial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credencial")
    private Long idCredencial;

    @Column(name = "correo_user", nullable = false, unique = true, length = 50)
    private String correoUser;

    @Column(name = "contrasena_user", nullable = false, length = 255)
    private String contrasenaUser;

    @Column(name = "activo")
    private Boolean activo; 
}


