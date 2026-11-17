package com.tienda.tienda_online.dto;

public record CategoriaResponse(
    Long id,
    String nombre,
    String descripcion,
    Boolean activo
) {}
