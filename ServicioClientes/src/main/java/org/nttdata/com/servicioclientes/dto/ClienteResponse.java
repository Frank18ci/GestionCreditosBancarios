package org.nttdata.com.servicioclientes.dto;

import lombok.Builder;

@Builder
public record ClienteResponse(
    Long id,
    String nombre,
    String dni,
    String email,
    EstadoClienteResponse estadoCliente,
    String keycloakId
){}
