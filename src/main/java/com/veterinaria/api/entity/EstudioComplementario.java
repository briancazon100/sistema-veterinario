package com.veterinaria.api.entity;

import com.veterinaria.api.entity.enums.EstadoEstudio;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "estudios_complementarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudioComplementario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_estudio", nullable = false)
    private String tipoEstudio;

    private String descripcion;

    @Column(name = "fecha_solicitud",nullable = false)
    private LocalDate fechaSolicitud;

    @Column(name = "fecha_realizacion") //nullable para casos diferidos , osea en donde debera esperar el estudio y recien completar este campo
    private LocalDate fechaRealizacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEstudio estado;

    @Column(name = "resultado_informe", columnDefinition = "TEXT")
    private String resultadoInforme;

    @Column(name = "url_archivo") // Nullable si solo se emite la orden primero
    private String urlArchivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false)
    private ConsultaMedica consultaMedica;
}
