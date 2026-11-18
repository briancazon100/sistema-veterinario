package com.veterinaria.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "veterinarias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veterinaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String cuit; // O identificador único fiscal

    @Column(name = "estado")
    private boolean estado = true;
}