package org.nttdata.com.servicioprestamos.client.dto;

import lombok.Builder;

@Builder
public record TipoTransaccionResponse(Long id, String nombre) {}