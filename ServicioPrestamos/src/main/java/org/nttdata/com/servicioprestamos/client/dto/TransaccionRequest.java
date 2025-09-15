package org.nttdata.com.servicioprestamos.client.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionRequest {
    private BigDecimal monto;
    private String tipoTransaccion;
    private String descripcion;