package org.nttdata.com.servicioclientes.client.dto;

import lombok.Builder;

@Builder
public record EstadoCuentaResponse(Long id, String nombre) {}
