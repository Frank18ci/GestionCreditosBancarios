package org.nttdata.com.serviciocuentas.dto;

import jakarta.validation.constraints.NotBlank;

public record EstadoCuentaRequest(
    @NotBlank(message = "El nombre no puede estar vacío")
    String nombre
) {}