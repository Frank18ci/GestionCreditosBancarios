package org.nttdata.com.servicioprestamos.client.dto;

import lombok.Builder;

@Builder
public record EstadoClienteResponse(
        Long id,
        String estado
) {
}
