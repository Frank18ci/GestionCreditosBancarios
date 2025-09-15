package org.nttdata.com.servicioprestamos.client.dto;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
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
public class TransaccionRequest {
    @NotNull(message = "El ID de la cuenta no puede ser nulo")
    private Long cuentaId;
    @NotNull(message = "El ID del tipo de transacción no puede ser nulo")
    private Long tipoTransaccionId;
    @NotNull(message = "El monto no puede ser nulo")
    @Positive
    private BigDecimal monto;
    @NotNull(message = "La fecha no puede ser nula")
    private Date fecha;
    @NotBlank(message = "La referencia no puede estar vacía")
    private String referencia;
}
