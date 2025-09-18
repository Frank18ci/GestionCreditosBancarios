package org.nttdata.com.serviciocuentas.dto;

import lombok.Builder;

@Builder
public record TipoCuentaResponse(Long id, String nombre) {}