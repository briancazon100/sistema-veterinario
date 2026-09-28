package com.veterinaria.api.dtos.veterinarioDto;

public record VeterinarioResponseDto(
        Long id,
        String nombre,
        String apellido,
        String matricula,
        String especialidad
) {
}
