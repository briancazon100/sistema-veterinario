package com.veterinaria.api.dtos.UserDto;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
        @NotNull(message = "El nombre de usuario es obligatorio")
        String username,

        @NotNull(message = "La contraseña es obligatoria")
        String contraseña
) {
}
