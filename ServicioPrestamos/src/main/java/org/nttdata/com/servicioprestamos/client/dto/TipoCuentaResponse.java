package org.nttdata.com.servicioprestamos.client.dto;

import lombok.Builder;

@Builder
public record TipoCuentaResponse(Long id, String nombre) {}