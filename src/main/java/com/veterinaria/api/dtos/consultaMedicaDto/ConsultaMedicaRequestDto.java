package com.veterinaria.api.dtos.consultaMedicaDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConsultaMedicaRequestDto(

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        @NotNull(message = "El peso es obligatorio")
        Double peso,

        @NotNull(message = "La temperatura es obligatoria")
        Double temperatura,

        Integer frecuenciaCardiaca,

        String diagnostico,

        String observaciones,


        @NotNull(message = "La mascota es obligatoria")
        Long mascotaId,

        @NotNull(message = "El veterinario es obligatorio")
        Long veterinarioId



) {
}
