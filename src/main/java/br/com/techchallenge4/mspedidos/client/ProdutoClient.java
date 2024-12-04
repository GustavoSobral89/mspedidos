package br.com.techchallenge4.mspedidos.client;

import br.com.techchallenge4.mspedidos.model.ProdutoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "produtos-service", url = "http://localhost:8082") // URL do microsserviço de produtos
public interface ProdutoClient {

    @GetMapping("/produtos/{id}")
    ProdutoDTO getProdutoById(@PathVariable("id") Long id);

    @PutMapping("/produtos/verificar-estoque/{id}/quantidade/{quantidade}")
    void atualizarEstoque(@PathVariable Long id, @PathVariable int quantidade);
}
