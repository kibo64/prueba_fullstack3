package com.tienda.tienda_online.repository;

import com.tienda.tienda_online.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioUsername(String username);
}
