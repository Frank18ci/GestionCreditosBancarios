package org.nttdata.com.servicioprestamos.client;

import org.nttdata.com.servicioprestamos.client.dto.TransaccionRequest;
import org.nttdata.com.servicioprestamos.client.dto.TransaccionResponse;
import org.nttdata.com.servicioprestamos.client.dto.CuentaRequest;
import org.nttdata.com.servicioprestamos.client.dto.CuentaResponse;
import org.nttdata.com.servicioprestamos.configuration.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "ServicioCuentas", configuration = FeignClientConfig.class)
public interface CuentaClient {
    @GetMapping("/{id}")
    TransaccionRequest getCuentaById(@PathVariable("id") Long cuentaId);
    @PostMapping("/{id}/transacciones")
    TransaccionRequest registrarTransaccion(@PathVariable("id") Long cuentaId, @RequestBody TransaccionRequest request);
    @GetMapping("/cuentas/{id}")
    CuentaResponse getCuentaById(@PathVariable Long id);
    @PutMapping("/cuentas/{id}")
    CuentaResponse updateCuenta(@PathVariable Long id, CuentaRequest cuentaRequest);
}
