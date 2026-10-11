package com.veterinaria.api.dtos.UserDto;

public record JwtResponseDto(
        String token,
        String tipo,
        String username,
        String rol
) {
}
