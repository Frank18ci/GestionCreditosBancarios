package org.nttdata.com.servicioprestamos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.Date;


@Builder
public record CuotaRequest(
    @NotNull(message = "El ID del préstamo no puede ser nulo")
    Long prestamoId,
    @NotNull(message = "El número de cuota no puede ser nulo")
    @Positive(message = "El número de cuota debe ser un valor positivo")
    Integer numero,
    @NotNull(message = "La fecha de vencimiento no puede ser nula")
    Date fechaVencimiento,
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser un valor positivo")
    BigDecimal monto,
    @NotNull(message = "El ID del estado de la cuota no puede ser nulo")
    Long estadoCuotaId
){

        }
