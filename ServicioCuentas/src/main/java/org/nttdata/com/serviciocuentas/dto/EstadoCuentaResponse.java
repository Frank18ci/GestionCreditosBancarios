package org.nttdata.com.serviciocuentas.dto;

import lombok.Builder;

@Builder
public record EstadoCuentaResponse(Long id, String nombre) {}
