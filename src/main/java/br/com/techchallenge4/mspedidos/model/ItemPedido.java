package br.com.techchallenge4.mspedidos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")  // Adicionando a chave estrangeira explicitamente
    @JsonBackReference
    private Pedido pedido;

    private Long produtoId;  // ID do produto
    private Integer quantidade;  // Quantidade do produto no pedido
}