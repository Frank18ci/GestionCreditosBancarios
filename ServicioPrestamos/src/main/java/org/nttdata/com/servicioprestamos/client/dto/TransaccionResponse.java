package org.nttdata.com.servicioprestamos.client.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record TransaccionResponse(
        Long id,
        Long cuentaId,
        TipoTransaccionResponse tipoTransaccion,
        BigDecimal monto,
        Date fecha,
        String referencia
) {}