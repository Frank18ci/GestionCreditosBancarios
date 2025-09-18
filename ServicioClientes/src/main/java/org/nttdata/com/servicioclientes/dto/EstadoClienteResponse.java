package org.nttdata.com.servicioclientes.dto;

import lombok.Builder;

@Builder
public record EstadoClienteResponse(
        Long id,
        String estado
) {
}
