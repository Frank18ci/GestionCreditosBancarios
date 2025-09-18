package org.nttdata.com.serviciotransacciones.dto;

import lombok.Builder;

@Builder
public record TipoTransaccionResponse(Long id, String nombre) {}