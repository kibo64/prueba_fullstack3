package com.tienda.tienda_online.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record DetallePedidoRequest(

        @NotNull(message = "El ID del producto es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad

) {}