package com.veterinaria.api.dtos.clienteDto;


import jakarta.validation.constraints.NotBlank;

// dto para recibir lo que mande el cliente (No tiene ID porque se autogenera)
//ademas le metemos validacion para que el front no mande cagadas y le ahorramos eso a la base de datos
public record ClienteRequestDto(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    String apellido,

    @NotBlank(message = "El teléfono es obligatorio")
    String telefono,

    @NotBlank(message = "La dirección es obligatoria")
    String direccion
){}
