package br.com.techchallenge4.mspedidos.service;

import br.com.techchallenge4.mspedidos.client.ClienteClient;
import br.com.techchallenge4.mspedidos.client.ProdutoClient;
import br.com.techchallenge4.mspedidos.exception.PedidoNotFoundException;
import br.com.techchallenge4.mspedidos.model.ItemPedido;
import br.com.techchallenge4.mspedidos.model.Pedido;
import br.com.techchallenge4.mspedidos.model.ProdutoDTO;
import br.com.techchallenge4.mspedidos.repository.ItemPedidoRepository;
import br.com.techchallenge4.mspedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    @Autowired
    private ClienteClient clienteClient;
    @Autowired
    private ProdutoClient produtoClient;

    public void processarPedido(Pedido pedido) {
        // Verifica o estoque de cada item do pedido
        for (ItemPedido item : pedido.getItens()) {
            ProdutoDTO produto = produtoClient.getProdutoById(item.getProdutoId());
            if (produto.getQuantidadeestoque() < item.getQuantidade()) {
                throw new RuntimeException("Quantidade insuficiente para o produto: " + produto.getNome() + " de ID: " + item.getProdutoId());
            }
            item.setPedido(pedido);
        }

        //verificar se o cliente existe
        clienteClient.getClienteById(pedido.getClienteId());

        pedidoRepository.save(pedido);

        // Dá baixa no estoque de cada produto
        for (ItemPedido item : pedido.getItens()) {
            produtoClient.atualizarEstoque(item.getProdutoId(), item.getQuantidade());
        }
    }

    public Pedido getPedidoById(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.orElseThrow(() -> new PedidoNotFoundException(id));
    }

    // Método para obter todos os pedidos
    public List<Pedido> getAll() {
        return pedidoRepository.findAll();
    }

    public void delete(Long id) {
        // Verifica se o pedido existe antes de tentar deletar
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id)); // Lança exceção se não encontrado

        // Deleta o pedido
        pedidoRepository.delete(pedido);
    }
}
