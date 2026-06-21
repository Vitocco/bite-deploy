package com.example.inventario.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventario.assemblers.InventarioModelAssembler;
import com.example.inventario.dto.InventarioRequest;
import com.example.inventario.dto.InventarioResponse;
import com.example.inventario.service.InventarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Operaciones relacionadas con el inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final InventarioModelAssembler assembler;

    @Operation(summary = "Crear inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class)))
    })
    @PostMapping
    public ResponseEntity<InventarioResponse> crear(@RequestBody InventarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crearInventario(request));
    }

    @Operation(summary = "Obtener todos los inventarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inventarios obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class)))
    })
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<InventarioResponse>> obtenerTodos() {
        List<EntityModel<InventarioResponse>> lista = inventarioService.obtenerTodos()
                .stream().map(assembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(lista,
                linkTo(methodOn(InventarioController.class).obtenerTodos()).withSelfRel());
    }

    @Operation(summary = "Obtener inventario por id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventario encontrado",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<InventarioResponse> obtenerPorId(@PathVariable Long id) {
        return assembler.toModel(inventarioService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener inventario por producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventario encontrado",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @GetMapping(value = "/producto/{productoId}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<InventarioResponse> obtenerPorProductoId(@PathVariable Long productoId) {
        return assembler.toModel(inventarioService.obtenerPorProductoId(productoId));
    }

    @Operation(summary = "Obtener inventarios con stock bajo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inventarios con stock bajo",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class)))
    })
    @GetMapping(value = "/stock/bajo", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<InventarioResponse>> obtenerStockBajo() {
        List<EntityModel<InventarioResponse>> lista = inventarioService.obtenerStockBajo()
                .stream().map(assembler::toModel).collect(Collectors.toList());
        return CollectionModel.of(lista,
                linkTo(methodOn(InventarioController.class).obtenerStockBajo()).withSelfRel());
    }

    @Operation(summary = "Actualizar inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = InventarioResponse.class))),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<InventarioResponse> actualizar(@PathVariable Long id, @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar inventario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Inventario eliminado exitosamente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Inventario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
