package com.tienda.tienda_online.service;

import com.tienda.tienda_online.dto.CategoriaRequest;
import com.tienda.tienda_online.dto.CategoriaResponse;
import com.tienda.tienda_online.entity.Categoria;
import com.tienda.tienda_online.repository.CategoriaRepository;
import com.tienda.tienda_online.repository.ProductoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    public CategoriaResponse crearCategoria(CategoriaRequest request) {

        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La categoría ya existe");
        }

        Categoria categoria = new Categoria();

        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());
        categoria.setActivo(request.activo() != null ? request.activo() : true);

        return convertirAResponse(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategoriasActivas() {
        return categoriaRepository.findByActivoTrue()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtenerCategoria(Long id) {
        return convertirAResponse(buscarPorId(id));
    }

    public CategoriaResponse actualizarCategoria(Long id, CategoriaRequest request) {
        Categoria categoria = buscarPorId(id);

        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otra categoría con ese nombre");
        }

        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        if (request.activo() != null) {
            categoria.setActivo(request.activo());
        }

        return convertirAResponse(categoriaRepository.save(categoria));
    }

    public void eliminarCategoria(Long id) {

        Categoria categoria = buscarPorId(id);

        if (productoRepository.existsByCategoriaId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar la categoría porque tiene productos asociados");
        }

        categoriaRepository.delete(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> buscarPorNombre(String nombre) {
        return categoriaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada con ID: " + id));
    }

    private CategoriaResponse convertirAResponse(Categoria categoria) {
        return new CategoriaResponse(
            categoria.getId(),
            categoria.getNombre(),
            categoria.getDescripcion(),
            categoria.getActivo()
        );
    }
}
