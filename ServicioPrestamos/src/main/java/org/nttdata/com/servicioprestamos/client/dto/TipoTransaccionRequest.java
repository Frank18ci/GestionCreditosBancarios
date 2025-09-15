package org.nttdata.com.servicioprestamos.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoTransaccionRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;
}
