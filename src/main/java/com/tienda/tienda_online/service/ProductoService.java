package com.tienda.tienda_online.service;

import com.tienda.tienda_online.dto.ProductoRequest;
import com.tienda.tienda_online.dto.ProductoResponse;
import com.tienda.tienda_online.entity.Producto;
import com.tienda.tienda_online.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ProductoResponse crearProducto(ProductoRequest request) {

        Producto producto = new Producto();

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setImagenUrl(request.imagenUrl());
        producto.setActivo(request.activo() != null ? request.activo() : true
        );

        Producto guardado = productoRepository.save(producto);

        return convertirAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductos() {

        return productoRepository.findAll().stream().map(this::convertirAResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosActivos() {

        return productoRepository.findByActivoTrue().stream().map(this::convertirAResponse).toList();
    }


    @Transactional(readOnly = true)
    public ProductoResponse obtenerProducto(Long id) {
        Producto producto = buscarPorId(id);

        return convertirAResponse(producto);
    }


    public ProductoResponse actualizarProducto(Long id,ProductoRequest request) {
        Producto producto = buscarPorId(id);

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setImagenUrl(request.imagenUrl());

        if (request.activo() != null) {
            producto.setActivo(request.activo());
        }
        Producto actualizado = productoRepository.save(producto);

        return convertirAResponse(actualizado);
    }

    public void eliminarProducto(Long id) {
        Producto producto = buscarPorId(id);

        productoRepository.delete(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorNombre(String nombre) {

        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream().map(this::convertirAResponse).toList();
    }

    private Producto buscarPorId(Long id) {

        return productoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado con ID: " + id));
    }

    private ProductoResponse convertirAResponse(Producto producto) {

        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getActivo(),
                producto.getFechaCreacion()
        );
    }
}
