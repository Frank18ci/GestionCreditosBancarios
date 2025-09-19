package org.nttdata.com.serviciocuentas.client.dto;

import lombok.Builder;

@Builder
public record EstadoClienteResponse(
        Long id,
        String estado
) {
}
