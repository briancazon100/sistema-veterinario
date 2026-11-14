package com.veterinaria.api.dtos.UserDto;

import com.veterinaria.api.entity.Rol;

public record UsuarioInfoResponseDto(
        Long id,
        String username,
        Rol rol,
        boolean estado
) {
}
