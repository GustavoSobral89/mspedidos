package br.com.techchallenge4.mspedidos;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import br.com.techchallenge4.mspedidos.client.ClienteClient;
import br.com.techchallenge4.mspedidos.client.ProdutoClient;
import br.com.techchallenge4.mspedidos.exception.PedidoNotFoundException;
import br.com.techchallenge4.mspedidos.model.ClienteDTO;
import br.com.techchallenge4.mspedidos.model.ItemPedido;
import br.com.techchallenge4.mspedidos.model.Pedido;
import br.com.techchallenge4.mspedidos.model.ProdutoDTO;
import br.com.techchallenge4.mspedidos.repository.ItemPedidoRepository;
import br.com.techchallenge4.mspedidos.repository.PedidoRepository;
import br.com.techchallenge4.mspedidos.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

public class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProdutoClient produtoClient;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedido;
    private ItemPedido itemPedido;

    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Preparar dados para os testes
        itemPedido = new ItemPedido();
        itemPedido.setProdutoId(1L);
        itemPedido.setQuantidade(2);

        pedido = new Pedido(1L, Collections.singletonList(itemPedido));
        pedido.setId(1L);

        // Inicializando ProdutoDTO com o construtor existente
        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Produto 1");
        produtoDTO.setDescricao("Descrição do Produto");
        produtoDTO.setPreco(BigDecimal.valueOf(100));
        produtoDTO.setQuantidadeestoque(10);  // Estoque suficiente
        produtoDTO.setCreatedatetime(LocalDateTime.now());
    }

    @Test
    void testProcessarPedidoComEstoqueSuficiente() {
        // Mocking the ProdutoClient response
        when(produtoClient.getProdutoById(1L)).thenReturn(produtoDTO);

        // Mocking the ClienteClient response
        when(clienteClient.getClienteById(1L)).thenReturn(new ClienteDTO(1L));

        // Mocking the PedidoRepository save method
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Test the method
        pedidoService.processarPedido(pedido);

        // Verificar se os métodos foram chamados corretamente
        verify(produtoClient, times(1)).getProdutoById(1L);
        verify(clienteClient, times(1)).getClienteById(1L);
        verify(pedidoRepository, times(1)).save(pedido);
        verify(produtoClient, times(1)).atualizarEstoque(1L, 2);
    }

    @Test
    void testProcessarPedidoComEstoqueInsuficiente() {
        // Mocking o ProdutoClient para retornar um produto com estoque insuficiente
        produtoDTO.setQuantidadeestoque(1); // Estoque 1, mas o pedido solicita 2

        // Mocking a resposta do ProdutoClient
        when(produtoClient.getProdutoById(1L)).thenReturn(produtoDTO);

        // Testa o método, esperando que a exceção seja lançada
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.processarPedido(pedido);
        });

        // Verifica se a mensagem da exceção está correta
        assertEquals("Quantidade insuficiente para o produto: Produto 1 de ID: 1", exception.getMessage());
    }

    @Test
    void testProcessarPedidoClienteNaoEncontrado() {
        // Mocking the ProdutoClient response
        when(produtoClient.getProdutoById(1L)).thenReturn(produtoDTO);

        // Mocking the ClienteClient response to simulate a missing client
        when(clienteClient.getClienteById(1L)).thenThrow(new RuntimeException("Cliente não encontrado"));

        // Test the method, expecting exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.processarPedido(pedido);
        });

        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    void testGetPedidoById() {
        // Mocking PedidoRepository to return a pedido
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Test the method
        Pedido result = pedidoService.getPedidoById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetPedidoByIdNotFound() {
        // Mocking PedidoRepository to return empty
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // Test the method, expecting exception
        PedidoNotFoundException exception = assertThrows(PedidoNotFoundException.class, () -> {
            pedidoService.getPedidoById(1L);
        });

        assertEquals("Pedido não encontrado com o ID: 1", exception.getMessage());
    }

    @Test
    void testDeletePedido() {
        // Mocking PedidoRepository to return a pedido
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Test the delete method
        pedidoService.delete(1L);

        // Verifying if delete was called
        verify(pedidoRepository, times(1)).delete(pedido);
    }

    @Test
    void testDeletePedidoNotFound() {
        // Mocking PedidoRepository to return empty
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        // Test the delete method, expecting exception
        PedidoNotFoundException exception = assertThrows(PedidoNotFoundException.class, () -> {
            pedidoService.delete(1L);
        });

        assertEquals("Pedido não encontrado com o ID: 1", exception.getMessage());
    }

    @Test
    void testCalcularTotal() {
        // Mocking ProdutoClient response
        when(produtoClient.getProdutoById(1L)).thenReturn(produtoDTO);

        // Test the calcularTotal method
        BigDecimal total = pedido.calcularTotal(produtoClient);

        assertNotNull(total);
        assertEquals(BigDecimal.valueOf(200), total);  // 100 * 2
    }
}
