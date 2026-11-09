package com.veterinaria.api.dtos.estudiosComplemenetariosDto;

import java.time.LocalDate;

public record ResultadoEstudiosDto(
        String resultadoInforme,
        LocalDate fechaRealizacion
) {
}
