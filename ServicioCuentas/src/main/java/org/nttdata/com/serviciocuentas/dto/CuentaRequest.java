package org.nttdata.com.serviciocuentas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CuentaRequest(
    @NotNull(message = "El ID del cliente no puede ser nulo")
    Long clienteId,
    @NotNull(message = "El ID del tipo de cuenta no puede ser nulo")
    Long tipoCuentaId,
    @NotNull(message = "El saldo no puede ser nulo")
    Long estadoCuentaId,
    @NotNull(message = "El saldo no puede ser nulo")
    @Positive(message = "El saldo debe ser un valor positivo")
    BigDecimal saldo
) {}