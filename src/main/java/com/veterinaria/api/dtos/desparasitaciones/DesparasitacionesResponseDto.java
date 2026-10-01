package com.veterinaria.api.dtos.desparasitaciones;

import java.time.LocalDate;

//DTO para empaquetar los atributos  que vamos a devolverle al front
public record DesparasitacionesResponseDto(
        Long id,
        String productoUsado,
        LocalDate fechaAplicacion,
        LocalDate fechaProximaDosis,
        Long mascotaId
) {
}
