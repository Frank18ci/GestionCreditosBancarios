package org.nttdata.com.serviciotransacciones.client.dto;

import lombok.Builder;

@Builder
public record EstadoCuentaResponse(Long id, String nombre) {}
