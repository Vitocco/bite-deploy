package com.example.inventario.config;

import java.time.LocalDateTime;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.inventario.model.Inventario;
import com.example.inventario.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InventarioDataLoader {

    private final InventarioRepository inventarioRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void load() {
        if (inventarioRepository.count() < 10) {
            inventarioRepository.save(new Inventario(null, 8L, "Huevos",  120, 24, "unidad", LocalDateTime.now()));
            inventarioRepository.save(new Inventario(null, 9L, "Queso",    18,  4, "kg",     LocalDateTime.now()));
            inventarioRepository.save(new Inventario(null, 10L, "Azúcar",  35,  8, "kg",     LocalDateTime.now()));
        }
    }
}
