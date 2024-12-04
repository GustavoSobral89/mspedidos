package br.com.techchallenge4.mspedidos;

import br.com.techchallenge4.mspedidos.client.ProdutoClient;
import br.com.techchallenge4.mspedidos.controller.PedidoController;
import br.com.techchallenge4.mspedidos.model.ItemPedido;
import br.com.techchallenge4.mspedidos.model.Pedido;
import br.com.techchallenge4.mspedidos.model.ProdutoDTO;
import br.com.techchallenge4.mspedidos.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @Mock
    private ProdutoClient produtoClient;

    @InjectMocks
    private PedidoController pedidoController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ProdutoDTO produtoDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController).build();
        objectMapper = new ObjectMapper();

        // Inicializando ProdutoDTO com o construtor existente
        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Produto 1");
        produtoDTO.setDescricao("Descrição do Produto");
        produtoDTO.setPreco(BigDecimal.valueOf(100));
        produtoDTO.setQuantidadeestoque(10);  // Estoque suficiente
        produtoDTO.setCreatedatetime(LocalDateTime.now());
    }

    @Test
    public void testRealizarPedido_Sucesso() throws Exception {
        // Dados mockados
        Pedido pedido = new Pedido();
        pedido.setItens(List.of(new ItemPedido(1L, new Pedido(), 1L, 2)));

        // Simulando que o método processarPedido não retorna nada, mas precisa ser chamado
        Mockito.doNothing().when(pedidoService).processarPedido(any(Pedido.class));

        // Simulando requisição POST
        mockMvc.perform(MockMvcRequestBuilders.post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Pedido realizado com sucesso."));

        // Verificando se o pedido foi processado
        verify(pedidoService).processarPedido(any(Pedido.class));
    }

    @Test
    public void testRealizarPedido_Falha() throws Exception {
        // Dados mockados
        Pedido pedido = new Pedido();
        pedido.setItens(List.of(new ItemPedido(1L, new Pedido(), 1L, 2)));

        // Simulando que o método processarPedido lança uma exceção
        Mockito.doThrow(new RuntimeException("Dados inválidos")).when(pedidoService).processarPedido(any(Pedido.class));

        // Simulando requisição POST
        mockMvc.perform(MockMvcRequestBuilders.post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Dados inválidos"));

        // Verificando se o método foi chamado corretamente e gerou a falha
        verify(pedidoService).processarPedido(any(Pedido.class));
    }

    @Test
    public void testGetPedido_Sucesso() throws Exception {
        // Dados mockados
        Pedido pedido = new Pedido(1L, List.of(new ItemPedido(1L, new Pedido(), 1L, 2)));
        when(pedidoService.getPedidoById(1L)).thenReturn(pedido);
        when(produtoClient.getProdutoById(1L)).thenReturn(produtoDTO);

        // Simulando requisição GET
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pedido").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(200))
                .andReturn();

        // Verificando se o método foi chamado corretamente
        verify(pedidoService).getPedidoById(1L);
        verify(produtoClient).getProdutoById(1L);
    }

    @Test
    public void testGetPedido_NaoEncontrado() throws Exception {
        // Simulando que não encontra o pedido
        when(pedidoService.getPedidoById(1L)).thenReturn(null);

        // Simulando requisição GET
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testGetAllPedidos_Sucesso() throws Exception {
        // Dados mockados
        Pedido pedido = new Pedido(1L, List.of(new ItemPedido(1L, new Pedido(), 1L, 2)));
        when(pedidoService.getAll()).thenReturn(List.of(pedido));
        when(produtoClient.getProdutoById(1L)).thenReturn(produtoDTO);

        // Simulando requisição GET
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].pedido").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].total").value(200));
    }

    @Test
    public void testGetAllPedidos_NaoEncontrado() throws Exception {
        // Simulando que não encontra nenhum pedido
        when(pedidoService.getAll()).thenReturn(List.of());

        // Simulando requisição GET
        mockMvc.perform(MockMvcRequestBuilders.get("/pedidos"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testDeletePedido_Sucesso() throws Exception {
        // Simulando a exclusão
        mockMvc.perform(MockMvcRequestBuilders.delete("/pedidos/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        // Verificando se o método delete foi chamado
        verify(pedidoService).delete(1L);
    }

    @Test
    public void testDeletePedido_NaoEncontrado() throws Exception {
        // Simulando que não encontra o pedido para excluir
        doThrow(new RuntimeException("Pedido não encontrado")).when(pedidoService).delete(1L);

        // Simulando requisição DELETE
        mockMvc.perform(MockMvcRequestBuilders.delete("/pedidos/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
