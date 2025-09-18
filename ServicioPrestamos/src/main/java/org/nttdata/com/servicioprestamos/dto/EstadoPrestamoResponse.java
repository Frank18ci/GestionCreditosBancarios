package org.nttdata.com.servicioprestamos.dto;

import lombok.Builder;

@Builder
public record EstadoPrestamoResponse(Long id, String nombre) {}