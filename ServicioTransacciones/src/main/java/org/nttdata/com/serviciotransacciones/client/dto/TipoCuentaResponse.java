package org.nttdata.com.serviciotransacciones.client.dto;

import lombok.Builder;

@Builder
public record TipoCuentaResponse(Long id, String nombre) {}