package org.nttdata.com.servicioprestamos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EstadoPrestamoRequest(
    @NotBlank(message = "El nombre no puede estar vacío")
    String nombre
) {}