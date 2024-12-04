package br.com.techchallenge4.mspedidos.controller;

import br.com.techchallenge4.mspedidos.client.ProdutoClient;
import br.com.techchallenge4.mspedidos.model.ItemPedido;
import br.com.techchallenge4.mspedidos.model.Pedido;
import br.com.techchallenge4.mspedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ProdutoClient produtoClient;

    public PedidoController(PedidoService pedidoService, ProdutoClient produtoClient) {
        this.pedidoService = pedidoService;
        this.produtoClient = produtoClient;
    }

    @Operation(summary = "Cria um novo pedido", description = "Cria um novo pedido associando o cliente e os itens fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a criação do pedido")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<String> realizarPedido(@RequestBody Pedido pedido) {
        try {
            pedidoService.processarPedido(new Pedido(pedido.getClienteId(), pedido.getItens()));
            return ResponseEntity.status(HttpStatus.CREATED).body("Pedido realizado com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Obtém um pedido pelo ID", description = "Recupera os detalhes de um pedido baseado no ID fornecido, incluindo o total do pedido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPedido(@PathVariable Long id) {
        Pedido pedido = pedidoService.getPedidoById(id);

        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        // Calculando o total do pedido
        BigDecimal total = pedido.calcularTotal(produtoClient);

        // Montando a resposta com o total
        Map<String, Object> response = new HashMap<>();
        response.put("pedido", pedido);
        response.put("total", total);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtém todos os pedidos", description = "Recupera todos os pedidos com seus respectivos totais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum pedido encontrado")
    })
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPedidos() {
        List<Pedido> pedidos = pedidoService.getAll();

        if (pedidos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Calculando o total para cada pedido
        List<Map<String, Object>> response = new ArrayList<>();

        for (Pedido pedido : pedidos) {
            BigDecimal total = pedido.calcularTotal(produtoClient);

            Map<String, Object> pedidoMap = new HashMap<>();
            pedidoMap.put("pedido", pedido);
            pedidoMap.put("total", total);

            response.add(pedidoMap);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Exclui um pedido", description = "Exclui um pedido do sistema com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado para exclusão")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna 204 No Content
    public void deletePedido(@PathVariable Long id) {
        pedidoService.delete(id);
    }

    // Tratamento global para exceções
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
