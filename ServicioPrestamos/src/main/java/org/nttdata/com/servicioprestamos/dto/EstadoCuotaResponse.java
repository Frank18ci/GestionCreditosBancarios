package org.nttdata.com.servicioprestamos.dto;

import lombok.Builder;

@Builder
public record EstadoCuotaResponse(Long id, String nombre) {}