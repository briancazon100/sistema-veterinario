package com.veterinaria.api.dtos.turnoDto;

import com.veterinaria.api.entity.enums.EstadoTurno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

//dto para empaquetar lo que vamos a recibir del front, agregamos validaciones como que no vengan campos vacios
public record TurnoRequestDto(
        @NotNull(message = "La fecha y hora es obligatoria")
        LocalDateTime fechaHora,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,



        @NotNull(message = "El id de la mascota es obligatorio")
        Long mascotaId,

        @NotNull(message = "El id del veterinario es obligatorio")
        Long veterinarioId
) {




}
