package com.veterinaria.api.dtos;

//dto para mandarle al cliente (Acá sí va el ID, y podemos ocultar datos si quisiéramos)
public record ClienteResponseDto(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        String direccion

) {
}
