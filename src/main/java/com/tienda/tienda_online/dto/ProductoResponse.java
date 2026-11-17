package com.tienda.tienda_online.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponse(

    Long id,
    String nombre,
    String descripcion,
    BigDecimal precio,
    Integer stock,
    String imagenUrl,
    Boolean activo,
    LocalDateTime fechaCreacion

) {
}
