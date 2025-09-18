package org.nttdata.com.servicioprestamos.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record PrestamoResponse(
    Long id,
    Long clienteId,
    Long cuentaId,
    BigDecimal monto,
    Integer plazoMeses,
    BigDecimal tasaInteres,
    EstadoPrestamoResponse estadoPrestamo,
    Date fechaDesembolso
) {}