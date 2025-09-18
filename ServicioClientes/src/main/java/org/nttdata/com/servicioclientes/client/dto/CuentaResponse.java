package org.nttdata.com.servicioclientes.client.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CuentaResponse(
        Long id,
        Long clienteId,
        TipoCuentaResponse tipoCuenta,
        EstadoCuentaResponse estadoCuenta,
        BigDecimal saldo
) {}
