package com.tienda.tienda_online.repository;

import com.tienda.tienda_online.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Categoria> findByActivoTrue();

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
}
