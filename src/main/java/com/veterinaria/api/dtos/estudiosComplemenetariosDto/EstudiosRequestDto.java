package com.veterinaria.api.dtos.estudiosComplemenetariosDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

//dto de entrada , cuando hace la solicitud
public record EstudiosRequestDto(
        @NotBlank(message = "El tipo de estudio es obligatorio (ej: Radiografía, Hemograma)")
        String tipoEstudio,

        String descripcion,


        @NotNull(message = "El id de la consulta es obligatorio")
        Long consultaId
) {
}
