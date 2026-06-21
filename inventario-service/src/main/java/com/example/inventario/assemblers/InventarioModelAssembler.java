package com.example.inventario.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.inventario.controller.InventarioController;
import com.example.inventario.dto.InventarioResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class InventarioModelAssembler implements RepresentationModelAssembler<InventarioResponse, EntityModel<InventarioResponse>> {

    @Override
    public EntityModel<InventarioResponse> toModel(InventarioResponse inventario) {
        return EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class).obtenerPorId(inventario.getId())).withSelfRel(),
                linkTo(methodOn(InventarioController.class).obtenerTodos()).withRel("inventarios"));
    }
}
