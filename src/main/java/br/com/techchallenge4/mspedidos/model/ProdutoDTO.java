package br.com.techchallenge4.mspedidos.model;

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
public class ProdutoDTO {
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int quantidadeestoque;
    private LocalDateTime createdatetime;

    @OneToMany(mappedBy = "produto")
    private List<ItemPedido> itensPedido;
}
