package com.tienda.tienda_online.controller;

import com.tienda.tienda_online.dto.PedidoRequest;
import com.tienda.tienda_online.dto.PedidoResponse;
import com.tienda.tienda_online.entity.Pedido;
import com.tienda.tienda_online.service.PedidoService;
import com.tienda.tienda_online.dto.DetallePedidoResponse;
import com.tienda.tienda_online.entity.DetallePedido;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse crearPedido(@Valid @RequestBody PedidoRequest request, Authentication authentication) {

        Pedido pedido = pedidoService.crearPedido(
                authentication.getName(),
                request.direccionEntrega(),
                request.items().stream().map(item -> new PedidoService.ItemPedido(item.productoId(),item.cantidad())).toList()
        );
        return convertirRespuesta(pedido);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPedido(
            @PathVariable Long id,
            Authentication authentication) {

        boolean puedeVerTodos = puedeVerTodos(authentication);
        Pedido pedido = pedidoService.buscarPedido(id,authentication.getName(), puedeVerTodos);

        return convertirRespuesta(pedido);
    }

    @GetMapping
    public List<PedidoResponse> listarPedidos(Authentication authentication) {

        boolean puedeVerTodos = puedeVerTodos(authentication);

        return pedidoService.listarPedidos(authentication.getName(),puedeVerTodos).stream().map(this::convertirRespuesta).toList();
    }

    private boolean puedeVerTodos(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority ->authority.getAuthority().equals("ROLE_ADMINISTRADOR") ||authority.getAuthority().equals("ROLE_VENDEDOR"));
    }

    private PedidoResponse convertirRespuesta(Pedido pedido) {
        return new PedidoResponse(
                pedido.getId(),
                pedido.getUsuario().getUsername(),
                pedido.getFechaPedido(),
                pedido.getDireccionEntrega(),
                pedido.getSubtotal(),
                pedido.getImpuesto(),
                pedido.getTotal(),
                pedido.getEstado(),
                pedido.getMetodoPago()
        );
    }

    @GetMapping("/{id}/detalles")
    public List<DetallePedidoResponse> listarDetalles(@PathVariable Long id, Authentication authentication) {

    boolean puedeVerTodos = puedeVerTodos(authentication);

    return pedidoService.listarDetalles(id, authentication.getName(),puedeVerTodos).stream().map(this::convertirDetalleRespuesta).toList();
    }

    private DetallePedidoResponse convertirDetalleRespuesta( DetallePedido detalle) {
        return new DetallePedidoResponse(
                detalle.getId(),
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
        );
    }
}
