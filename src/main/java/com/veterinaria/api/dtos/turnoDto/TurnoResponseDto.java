package com.veterinaria.api.dtos.turnoDto;

import com.veterinaria.api.entity.enums.EstadoTurno;

import java.time.LocalDateTime;

//dto para empaquetar lo que le vamos a devolver al front
public record TurnoResponseDto(
        Long id,
        LocalDateTime fechaHora,
        String motivo,
        EstadoTurno estado,
        Long mascotaId,
        Long veterinarioId
) {
}
