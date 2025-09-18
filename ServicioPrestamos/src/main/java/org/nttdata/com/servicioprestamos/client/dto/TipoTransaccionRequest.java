package org.nttdata.com.servicioprestamos.client.dto;

import jakarta.validation.constraints.NotBlank;

public record TipoTransaccionRequest(
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre
) {}