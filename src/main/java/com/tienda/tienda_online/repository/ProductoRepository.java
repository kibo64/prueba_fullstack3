package com.tienda.tienda_online.repository;

import com.tienda.tienda_online.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByPrecioBetween(
        java.math.BigDecimal minimo,
        java.math.BigDecimal maximo
    );
    boolean existsByCategoriaId(Long categoriaId);
}
