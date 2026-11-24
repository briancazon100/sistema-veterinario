package com.veterinaria.api.dtos.desparasitaciones;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DesparasitacionesEditRequestDto(
        @NotBlank(message = "El producto usado es obligatorio")
        String productoUsado,

        @NotNull(message = "La fecha de aplicación es obligatoria")
        LocalDate fechaAplicacion,

        @NotNull(message = "La fecha de la próxima dosis es obligatoria")
        LocalDate fechaProximaDosis
) {
}
