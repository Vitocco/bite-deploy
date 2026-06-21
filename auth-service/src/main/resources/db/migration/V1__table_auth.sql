CREATE TABLE credencial (
    id_credencial BIGINT AUTO_INCREMENT PRIMARY KEY,
    correo_user VARCHAR(50) NOT NULL UNIQUE,
    contrasena_user VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE token (
    id_token BIGINT AUTO_INCREMENT PRIMARY KEY,
    token TEXT NOT NULL,
    fecha_expiracion DATETIME NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    id_credencial BIGINT NOT NULL,
    CONSTRAINT FK_TOKEN_CREDENCIAL FOREIGN KEY (id_credencial) REFERENCES credencial(id_credencial)
);

INSERT INTO credencial (correo_user, contrasena_user, activo) 
VALUES ('vcortez6565@gmail.com', '$2a$12$WCB7qDoU3YduJ6W7EvW3f.zuT5QHP2rK1TP8YGUFhu9erptYJKej6', TRUE);