package org.nttdata.com.serviciocuentas.client;

import org.nttdata.com.serviciocuentas.client.dto.ClienteResponse;
import org.nttdata.com.serviciocuentas.configuration.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ServicioClientes", configuration = FeignClientConfig.class)
public interface ClienteClient {
    @GetMapping("/clientes/{id}")
    ClienteResponse getClienteById(@PathVariable Long id);
}
