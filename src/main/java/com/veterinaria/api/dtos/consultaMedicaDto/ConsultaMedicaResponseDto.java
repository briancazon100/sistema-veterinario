package com.veterinaria.api.dtos.consultaMedicaDto;

import java.time.LocalDate;

public record ConsultaMedicaResponseDto(
        Long id,
        LocalDate fecha,
        String motivo,
        Double peso,
        Double temperatura,
        Integer frecuenciaCardiaca,
        String diagnostico,
        String observaciones,
        Long mascotaId,
        String nombreMascota,
        Long veterinarioId,
        String nombreVeterinario
) {
}
