package com.veterinaria.api.dtos.veterinarioDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VeterinarioRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras")
        String apellido,

        @NotBlank(message = "La matrícula es obligatoria")

        String matricula,

        @NotBlank(message = "La especialidad es obligatoria")
        String especialidad
) {
}
