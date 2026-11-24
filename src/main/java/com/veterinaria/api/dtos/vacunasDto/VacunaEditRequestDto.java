package com.veterinaria.api.dtos.vacunasDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VacunaEditRequestDto(

        @NotBlank(message = "El nombre de la vacuna no debe estar vacío")
        String nombreVacuna,

        @NotNull(message = "La fecha de aplicación es obligatoria")
        LocalDate fechaAplicacion,

        @NotNull(message = "La fecha de la próxima dosis es obligatoria")
        LocalDate fechaProximaDosis
) {
}
