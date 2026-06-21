package com.example.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequest {

    private Long productoId;
    private String nombreProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    private String unidadMedida;
}
