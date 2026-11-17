package com.tienda.tienda_online.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductoRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
    String nombre,

    String descripcion,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    BigDecimal precio,

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser menor de 1")
    Integer stock,

    String imagenUrl,

    Boolean activo

) {
}
