
package com.tienda.tienda_online.service;

import com.tienda.tienda_online.entity.*;
import com.tienda.tienda_online.repository.*;

import java.security.SecureRandom;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {

    private static final BigDecimal IVA = new BigDecimal("0.19");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {

        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Pedido crearPedido(String username, String direccionEntrega, List<ItemPedido> items) {

        if (direccionEntrega == null || direccionEntrega.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La dirección de entrega es obligatoria");
        }

        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido debe contener productos");
        }

        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDireccionEntrega(direccionEntrega);
        pedido.setMetodoPago("SIMULADO");

        BigDecimal subtotal = BigDecimal.ZERO;
        List<DetallePedido> detalles = new ArrayList<>();

        for (ItemPedido item : items) {

            if (item.productoId() == null || item.cantidad() == null || item.cantidad() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto y la cantidad son obligatorios. La cantidad debe ser mayor a cero");
            }

            Producto producto = productoRepository.findById(item.productoId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + item.productoId()));

            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto no está disponible: " + producto.getNombre());
            }

            if (producto.getStock() == null || producto.getStock() < item.cantidad()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock insuficiente para: " + producto.getNombre()
                );
            }

            BigDecimal precio = producto.getPrecio();

            BigDecimal subtotalDetalle = precio.multiply(BigDecimal.valueOf(item.cantidad())).setScale(2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(subtotalDetalle);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(subtotalDetalle);

            detalles.add(detalle);
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        BigDecimal impuesto = subtotal.multiply(IVA).setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(impuesto).setScale(2, RoundingMode.HALF_UP);

        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setTotal(total);

        boolean pagoAprobado = RANDOM.nextInt(100) < 90;

        if (pagoAprobado) {
            pedido.setEstado("PAGADO");
        } else {
            pedido.setEstado("RECHAZADO");
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        detallePedidoRepository.saveAll(detalles);

        if (pagoAprobado) {
            for (DetallePedido detalle : detalles) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() - detalle.getCantidad()
                );
            }
        }
        return pedidoGuardado;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidos(String username, boolean puedeVerTodos) {

        if (puedeVerTodos) {
            return pedidoRepository.findAll();
        }

        return pedidoRepository.findByUsuarioUsername(username);
    }

    @Transactional(readOnly = true)
    public Pedido buscarPedido(Long id, String username, boolean puedeVerTodos) {

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

        if (!puedeVerTodos && !pedido.getUsuario().getUsername().equals(username)) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Pedido no encontrado");
        }

        return pedido;
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> listarDetalles(Long pedidoId, String username, boolean puedeVerTodos) {
        buscarPedido(pedidoId, username, puedeVerTodos);

        return detallePedidoRepository.findByPedidoId(pedidoId);
    }

    public record ItemPedido(Long productoId, Integer cantidad) {
    }
}
