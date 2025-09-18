package org.nttdata.com.servicioprestamos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EstadoCuotaRequest(
    @NotBlank(message = "El nombre no puede estar vacío")
    String nombre
) {}