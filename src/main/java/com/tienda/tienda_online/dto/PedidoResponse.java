package com.tienda.tienda_online.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponse(
        Long id,
        String username,
        LocalDateTime fechaPedido,
        String direccionEntrega,
        BigDecimal subtotal,
        BigDecimal impuesto,
        BigDecimal total,
        String estado,
        String metodoPago
) {}