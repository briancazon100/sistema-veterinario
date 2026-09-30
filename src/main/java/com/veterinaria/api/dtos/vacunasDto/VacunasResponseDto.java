package com.veterinaria.api.dtos.vacunasDto;

import java.time.LocalDate;

//dto para empaquetar los datos que vamos a retornar al front
public record VacunasResponseDto(
        Long id,
        String nombreVacuna,
        LocalDate fechaAplicacion,
        LocalDate fechaProximaDosis,
        Long veterinarioId,
        Long mascotaId
) {
}
