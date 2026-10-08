package com.veterinaria.api.dtos.estudiosComplemenetariosDto;

import com.veterinaria.api.entity.enums.EstadoEstudio;

import java.time.LocalDate;

//dto de salida
public record EstudiosResponseDto(
        Long id,
        String tipoEstudio,
        String descripcion,
        LocalDate fechaSolicitud,
        LocalDate fechaRealizacion,
        EstadoEstudio estadoEstudio,
        String resultadoInforme,
        String urlArchivo,
        Long consultaId
) {

}
