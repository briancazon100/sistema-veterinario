package com.veterinaria.api.dtos.mascotaDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record MascotaRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras")
        String nombre,

        @NotBlank(message = "La especie es obligatoria")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "La especie solo puede contener letras")
        String especie,

        @NotBlank(message = "La raza es obligatoria")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "La raza solo puede contener letras")
        String raza,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @PastOrPresent(message = "No es posible un fecha futura")
        LocalDate fechaNacimiento,

        @NotNull(message = "El ID del cliente (dueño) es obligatorio")
        Long clienteId
) {
}
