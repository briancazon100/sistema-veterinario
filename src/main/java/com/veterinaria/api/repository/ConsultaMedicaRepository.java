package com.veterinaria.api.repository;

import com.veterinaria.api.entity.ConsultaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaMedicaRepository extends JpaRepository<ConsultaMedica, Long> {

    Optional<ConsultaMedica> findByIdAndVeterinariaId(Long id, Long veterinariaId);
    // Obtener historial clínico de una mascota ordenado por fecha descendente
    List<ConsultaMedica> findByMascotaIdAndVeterinariaIdOrderByFechaDesc(Long mascotaId, Long veterinariaId);

    // Obtener todas las consultas realizadas por un veterinario específico
    List<ConsultaMedica> findByVeterinarioIdOrderByFechaDesc(Long veterinarioId);


    //obtener todas las consultas del mes actual
    List<ConsultaMedica> findByVeterinariaIdAndFechaBetween(Long veterinariaId, LocalDate inicio, LocalDate fin);

    boolean existsByIdAndVeterinariaId(Long id, Long veterinariaId);
}
