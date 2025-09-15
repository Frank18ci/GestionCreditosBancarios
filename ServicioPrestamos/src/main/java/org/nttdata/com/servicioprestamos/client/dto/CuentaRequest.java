package org.nttdata.com.servicioprestamos.client.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CuentaRequest {
    @NotNull(message = "El ID del cliente no puede ser nulo")
    private Long clienteId;
    @NotNull(message = "El ID del tipo de cuenta no puede ser nulo")
    private Long tipoCuentaId;
    @NotNull(message = "El saldo no puede ser nulo")
    private Long estadoCuentaId;
    @NotNull(message = "El saldo no puede ser nulo")
    @Positive
    private BigDecimal saldo;
}