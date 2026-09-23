package com.veterinaria.api.dtos.mascotaDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record MascotaRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La especie es obligatoria")
        String especie,

        @NotBlank(message = "La raza es obligatoria")
        String raza,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @PastOrPresent(message = "No es posible un fecha futura")
        LocalDate fecha_nacimiento,

        @NotNull(message = "El ID del cliente (dueño) es obligatorio")
        Long clienteId
) {
}
