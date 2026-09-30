package com.veterinaria.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "consultas_medicas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name="fecha")
    private LocalDate fecha;


    @Column(nullable = false, name="motivo")
    private String motivo;

    @Column(nullable = false, name="peso_kg")
    private Double  peso;

    @Column(nullable = false, name="temperatura")
    private Double temperatura;

    @Column(nullable = false, name="frecuencia_cardiaca")
    private Integer frecuencia_cardiaca;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mascota_id", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="veterinario_id", nullable = false)
    private Veterinario veterinario;
}
