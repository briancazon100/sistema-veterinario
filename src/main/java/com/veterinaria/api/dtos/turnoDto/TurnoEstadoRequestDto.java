package com.veterinaria.api.dtos.turnoDto;

import com.veterinaria.api.entity.enums.EstadoTurno;
import jakarta.validation.constraints.NotNull;

public record TurnoEstadoRequestDto(
        @NotNull(message = "El estado es obligatorio")
        EstadoTurno estadoTurno
) {
}
