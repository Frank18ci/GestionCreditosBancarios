package org.nttdata.com.servicioprestamos.client;

import org.nttdata.com.servicioprestamos.client.dto.TransaccionRequest;
import org.nttdata.com.servicioprestamos.client.dto.TransaccionResponse;
import org.nttdata.com.servicioprestamos.configuration.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "ServicioTransacciones", configuration = FeignClientConfig.class)
public interface TransaccionClient {
    @GetMapping("/transacciones/cuenta/{cuentaId}")
    List<TransaccionResponse> obteTransacciones(@PathVariable Long cuentaId);
    @PostMapping("/transacciones")
    TransaccionResponse crearTransaccion(TransaccionRequest transaccionRequest);
}
