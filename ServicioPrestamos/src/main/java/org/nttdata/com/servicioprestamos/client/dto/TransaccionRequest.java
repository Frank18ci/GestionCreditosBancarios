package org.nttdata.com.servicioprestamos.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record TransaccionRequest(
        @NotNull(message = "El ID de la cuenta no puede ser nulo")
        Long cuentaId,
        @NotNull(message = "El ID del tipo de transacción no puede ser nulo")
        Long tipoTransaccionId,
        @NotNull(message = "El monto no puede ser nulo")
        @Positive(message = "El monto debe ser un valor positivo")
        BigDecimal monto,
        @NotNull(message = "La fecha no puede ser nula")
        Date fecha,
        @NotBlank(message = "La referencia no puede estar vacía")
        String referencia
) {}