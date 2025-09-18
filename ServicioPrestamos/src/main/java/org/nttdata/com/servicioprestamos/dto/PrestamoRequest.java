package org.nttdata.com.servicioprestamos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record PrestamoRequest(
    @NotNull(message = "El ID del cliente no puede ser nulo")
    Long clienteId,
    @NotNull(message = "El ID de la cuenta no puede ser nulo")
    Long cuentaId,
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser un valor positivo")
    BigDecimal monto,
    @NotNull(message = "El plazo en meses no puede ser nulo")
    @Positive(message = "El plazo en meses debe ser un valor positivo")
    Integer plazoMeses,
    @NotNull(message = "La tasa de interés no puede ser nula")
    @Positive(message = "La tasa de interés debe ser un valor positivo")
    BigDecimal tasaInteres,
    Long estadoPrestamoId,
    Date fechaDesembolso
) {}
