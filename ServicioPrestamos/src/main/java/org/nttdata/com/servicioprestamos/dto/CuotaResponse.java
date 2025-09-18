package org.nttdata.com.servicioprestamos.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record CuotaResponse(
    Long id,
    PrestamoResponse prestamo,
    Integer numero,
    Date fechaVencimiento,
    BigDecimal monto,
    EstadoCuotaResponse estadoCuota
) {}