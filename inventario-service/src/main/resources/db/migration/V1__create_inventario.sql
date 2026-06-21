CREATE TABLE inventario (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id          BIGINT       NOT NULL,
    nombre_producto      VARCHAR(255) NOT NULL,
    stock_actual         INT          NOT NULL,
    stock_minimo         INT          NOT NULL,
    unidad_medida        VARCHAR(50)  NOT NULL,
    ultima_actualizacion DATETIME     NOT NULL
);
