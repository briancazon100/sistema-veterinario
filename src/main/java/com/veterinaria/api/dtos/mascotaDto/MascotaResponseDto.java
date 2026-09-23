package com.veterinaria.api.dtos.mascotaDto;

import java.time.LocalDate;

public record MascotaResponseDto(
        Long id,
        String nombre,
        String especie,
        String raza,
        LocalDate fechaNacimiento,
        Long clienteId,
        String nombreClienteCompleto // Muestra ej: "Juan Pérez" en lugar de solo el ID
) {
}
