package br.com.techchallenge4.mspedidos.repository;

import br.com.techchallenge4.mspedidos.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
