package org.nttdata.com.serviciocuentas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TipoCuentaRequest(@NotBlank(message = "El nombre no puede estar vacío")
                                String nombre) {}