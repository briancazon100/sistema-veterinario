package com.veterinaria.api.dtos.UserDto;

import jakarta.validation.constraints.NotNull;

public record RegistroRequestDto(
        @NotNull(message = "El nombre de usuario es obligatorio")
        String username,
        @NotNull(message = "La contraseña es obligatoria")
        String contraseña,

        @NotNull(message = "El rol es obligatorio")
        String rolNombre //ej: ROLE_USER  O  ROLE_ADMIN
) {
}
