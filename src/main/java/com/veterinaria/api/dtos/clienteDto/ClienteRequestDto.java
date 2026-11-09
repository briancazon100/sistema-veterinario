package com.veterinaria.api.dtos.clienteDto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// dto para recibir lo que mande el cliente (No tiene ID porque se autogenera)
//ademas le metemos validacion para que el front no mande cagadas y le ahorramos eso a la base de datos
public record ClienteRequestDto(
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras")
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras")
    String apellido,

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{8,15}$", message = "Teléfono inválido")
    String telefono,

    @NotBlank(message = "La dirección es obligatoria")
    String direccion
){}
