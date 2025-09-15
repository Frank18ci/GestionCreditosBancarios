package org.nttdata.com.servicioprestamos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuotaResponse {
    private Long id;
    private PrestamoResponse prestamo;
    private Integer numero;
    private Date fechaVencimiento;
    private BigDecimal monto;
    private EstadoCuotaResponse estadoCuota;
}
