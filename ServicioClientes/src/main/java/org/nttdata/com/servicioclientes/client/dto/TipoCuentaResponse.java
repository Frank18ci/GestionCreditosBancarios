package org.nttdata.com.servicioclientes.client.dto;

import lombok.Builder;

@Builder
public record TipoCuentaResponse(Long id, String nombre) {}