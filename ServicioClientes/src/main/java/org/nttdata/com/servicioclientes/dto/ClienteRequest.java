package org.nttdata.com.servicioclientes.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record ClienteRequest(
    @NotBlank(message = "El nombre no puede estar vacío")
    String nombre,
    @NotBlank(message = "El apellido no puede estar vacío")

    @Size(message = "El dni no puede tener más de 8 caracteres", max = 8, min = 8)
    String dni,
    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email no puede estar vacío")
    String email,
    @NotNull(message = "El ID del estado no puede ser nulo")
    Long estadoClienteId
){

        }
