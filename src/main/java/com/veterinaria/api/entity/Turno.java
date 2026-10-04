package com.veterinaria.api.entity;

import com.veterinaria.api.entity.enums.EstadoTurno;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "turnos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "motivo",  nullable = false)
    private String motivo;

    @Enumerated(EnumType.STRING)//le decís a Spring que use este Enum y lo guarde como texto en MySQL
    @Column(name = "estado", nullable = false)
    private EstadoTurno estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id")
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id")
    private Veterinario veterinario;


}
