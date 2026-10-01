package com.veterinaria.api.dtos.desparasitaciones;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

//DTO para empaquetar lo que vamos a recibir del front, le agregamos las validaciones necesarias
public record DesparasitacionesRequestDto(
        @NotBlank(message = "El producto usado es obligatorio")
        String productoUsado,

        @NotNull(message = "La fecha de aplicación es obligatoria")
        LocalDate fechaAplicacion,

        @NotNull(message = "La fecha de la próxima dosis es obligatoria")
        LocalDate fechaProximaDosis,

        @NotNull(message = "El id de la mascota es obligatorio")
        Long mascotaId
) {
}
