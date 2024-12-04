package br.com.techchallenge4.mspedidos.client;

import br.com.techchallenge4.mspedidos.model.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientes-service", url = "http://localhost:8081") // URL do microsserviço de clientes
public interface ClienteClient {

    @GetMapping("/clientes/{id}")
    ClienteDTO getClienteById(@PathVariable("id") Long id);
}
