package com.veterinaria.api.repository;

import com.veterinaria.api.entity.ConsultaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultaMedicaRepository extends JpaRepository<ConsultaMedica, Long> {
    // Obtener historial clínico de una mascota ordenado por fecha descendente
    List<ConsultaMedica> findByMascotaIdOrderByFechaDesc(Long mascotaId);

    // Obtener todas las consultas realizadas por un veterinario específico
    List<ConsultaMedica> findByVeterinarioIdOrderByFechaDesc(Long veterinarioId);
}
