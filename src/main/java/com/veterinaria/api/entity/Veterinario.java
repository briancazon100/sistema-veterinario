package com.veterinaria.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "veterinarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "nombre")
    private String nombre;

    @Column(nullable = false, name="apellido")
    private String apellido;

    @Column(nullable = false, name="matricula")
    private String matricula;

    @Column(nullable = false, name="especialidad")
    private String especialidad;
}
