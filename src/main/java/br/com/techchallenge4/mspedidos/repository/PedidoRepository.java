package br.com.techchallenge4.mspedidos.repository;

import br.com.techchallenge4.mspedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
