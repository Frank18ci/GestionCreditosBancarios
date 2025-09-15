package org.nttdata.com.servicioprestamos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CuotaRequest {
    @NotNull(message = "El ID del préstamo no puede ser nulo")
    private Long prestamoId;
    @NotNull(message = "El número de cuota no puede ser nulo")
    @Positive
    private Integer numero;
    @NotNull(message = "La fecha de vencimiento no puede ser nula")
    private Date fechaVencimiento;
    @NotNull(message = "El monto no puede ser nulo")
    @Positive
    private BigDecimal monto;
    @NotNull(message = "El ID del estado de la cuota no puede ser nulo")
    private Long estadoCuotaId;
}
