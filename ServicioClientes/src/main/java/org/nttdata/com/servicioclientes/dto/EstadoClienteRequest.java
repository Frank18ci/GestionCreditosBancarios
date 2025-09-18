package org.nttdata.com.servicioclientes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EstadoClienteRequest(
        @NotBlank(message = "El estado no puede estar vacío")
        String estado
) {
}
