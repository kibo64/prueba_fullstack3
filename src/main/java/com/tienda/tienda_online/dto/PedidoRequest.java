package com.tienda.tienda_online.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PedidoRequest(

        @NotBlank(message = "La dirección de entrega es obligatoria")
        String direccionEntrega,

        @NotEmpty(message = "El pedido debe contener al menos un producto")
        List<@Valid DetallePedidoRequest> items

) {}
