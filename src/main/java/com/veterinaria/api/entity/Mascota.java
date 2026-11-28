package com.veterinaria.api.entity;

import com.veterinaria.api.entity.enums.SexoMascota;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Table(name = "mascotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 90)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String especie;

    @Column(nullable = false, length = 50)
    private String raza;

    @Column(nullable=false, name = "fecha_nacimiento")
    private LocalDate fecha_nacimiento;

    // Relación Muchos a Uno con Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private boolean estado=true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinaria_id", nullable = false)
    private Veterinaria veterinaria;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Enumerated(EnumType.STRING)//le decís a Spring que use este Enum y lo guarde como texto en MySQL
    @Column(name = "sexo", nullable = false)
    private SexoMascota sexo;

    @Column(name = "esterilizado", nullable = false)
    private boolean esterilizado;

    @Column(name = "alergias_conocidas")
    private String alergiasConocidas;

    @Column(name = "condiciones_cronicas")
    private String condicionesCronicas;


}
