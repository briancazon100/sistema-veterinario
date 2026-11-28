package com.veterinaria.api.dtos.mascotaDto;

import com.veterinaria.api.entity.enums.SexoMascota;

import java.time.LocalDate;

public record MascotaResponseDto(
        Long id,
        String nombre,
        String especie,
        String raza,
        LocalDate fechaNacimiento,
        Long clienteId,
        String nombreClienteCompleto, // Muestra ej: "Juan Pérez" en lugar de solo el ID
        boolean estado,
        String fotoUrl,
        SexoMascota sexo,
        boolean esterilizado,
        String alergiasConocidas,
        String condicionesCronicas
) {
}
