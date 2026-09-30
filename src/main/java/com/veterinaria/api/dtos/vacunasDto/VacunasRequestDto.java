package com.veterinaria.api.dtos.vacunasDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

//dto para empaquetar lo que vamos a recibir del frontend
public record VacunasRequestDto(
        @NotBlank(message = "El nombre de la vacuna es obligatorio")
        String nombreVacuna,

        @NotNull(message = "La fecha de aplicación es obligatoria")
        LocalDate fechaAplicacion,

        @NotNull(message = "La fecha de la próxima dosis es obligatoria")
        LocalDate fechaProximaDosis,

        @NotNull(message = "El id del veterinario es obligatorio")
        Long veterinarioId,

        @NotNull(message = "El id de la mascota es obligatorio")
        Long mascotaId
) {
}
