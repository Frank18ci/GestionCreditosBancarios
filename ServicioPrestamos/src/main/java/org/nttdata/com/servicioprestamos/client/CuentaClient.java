package org.nttdata.com.servicioprestamos.client;

import org.nttdata.com.servicioprestamos.client.dto.TransaccionRequest;
import org.nttdata.com.servicioprestamos.client.dto.TransaccionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "servicio-cuentas", path = "/api/cuentas")
public interface CuentaClient {

    @GetMapping("/{id}")
    TransaccionRequest getCuentaById(@PathVariable("id") Long cuentaId);

    @PostMapping("/{id}/transacciones")
    TransaccionRequest registrarTransaccion(@PathVariable("id") Long cuentaId, @RequestBody TransaccionRequest request);
}
