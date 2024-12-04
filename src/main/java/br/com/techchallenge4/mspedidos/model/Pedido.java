package br.com.techchallenge4.mspedidos.model;

import br.com.techchallenge4.mspedidos.client.ProdutoClient;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // Evita a recursão infinita no lado dos "itens"
    private List<ItemPedido> itens;

    private LocalDateTime dataPedido;
    private String status;
    private String enderecoDestino;

    public Pedido(Long clienteId, List<ItemPedido> itens) {
        this.clienteId = clienteId;
        this.itens = itens;
        this.dataPedido = LocalDateTime.now();
        this.status = "Aguardando atribuição"; // Status inicial
    }

    // Método para calcular o total do pedido, utilizando o SOLID da própria classe ter essa responsabilidade
    public BigDecimal calcularTotal(ProdutoClient produtoClient) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : itens) {
            ProdutoDTO produto = produtoClient.getProdutoById(item.getProdutoId());
            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())));
        }

        return total;
    }
}
