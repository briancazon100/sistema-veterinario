package com.veterinaria.api.dtos.UserDto;

import com.veterinaria.api.entity.Rol;

public record UsuarioInfoResponseDto(
        String username,
        Rol rol
) {
}
