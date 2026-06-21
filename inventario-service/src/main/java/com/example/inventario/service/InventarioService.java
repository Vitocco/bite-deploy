package com.example.inventario.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventario.dto.InventarioRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.exception.NotFoundException;
import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private static final Logger log = LoggerFactory.getLogger(InventarioService.class);

    @Transactional
    public InventarioResponse crearInventario(InventarioRequest request) {
        Inventario inventario = new Inventario();
        inventario.setProductoId(request.getProductoId());
        inventario.setNombreProducto(request.getNombreProducto());
        inventario.setStockActual(request.getStockActual());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setUnidadMedida(request.getUnidadMedida());
        inventario.setUltimaActualizacion(LocalDateTime.now());

        Inventario guardado = inventarioRepository.save(inventario);
        log.info("Inventario creado para producto: {}", request.getProductoId());
        return mapToResponse(guardado);
    }

    @Transactional(readOnly = true)
    public InventarioResponse obtenerPorId(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Inventario no encontrado con id: " + id));
        return mapToResponse(inventario);
    }

    @Transactional(readOnly = true)
    public InventarioResponse obtenerPorProductoId(Long productoId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
            .orElseThrow(() -> new NotFoundException("Inventario no encontrado para producto: " + productoId));
        return mapToResponse(inventario);
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerTodos() {
        return inventarioRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public InventarioResponse actualizar(Long id, InventarioRequest request) {
        Inventario inventario = inventarioRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Inventario no encontrado con id: " + id));

        inventario.setProductoId(request.getProductoId());
        inventario.setNombreProducto(request.getNombreProducto());
        inventario.setStockActual(request.getStockActual());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setUnidadMedida(request.getUnidadMedida());
        inventario.setUltimaActualizacion(LocalDateTime.now());

        Inventario actualizado = inventarioRepository.save(inventario);
        log.info("Inventario actualizado con id: {}", id);
        return mapToResponse(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!inventarioRepository.existsById(id)) {
            throw new NotFoundException("Inventario no encontrado con id: " + id);
        }
        inventarioRepository.deleteById(id);
        log.info("Inventario eliminado con id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<InventarioResponse> obtenerStockBajo() {
        return inventarioRepository.findStockBajo()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private InventarioResponse mapToResponse(Inventario inventario) {
        return new InventarioResponse(
            inventario.getId(),
            inventario.getProductoId(),
            inventario.getNombreProducto(),
            inventario.getStockActual(),
            inventario.getStockMinimo(),
            inventario.getUnidadMedida()
        );
    }
}
