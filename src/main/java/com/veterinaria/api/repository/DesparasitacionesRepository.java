package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Desparasitaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DesparasitacionesRepository extends JpaRepository<Desparasitaciones, Long> {

    //listar todas las desparasitaciones del animal(id) en forma desc, de manera que el profesional pueda ver la ultima que se
    // le aplicó
    List<Desparasitaciones> findByMascotaIdAndVeterinariaIdOrderByFechaAplicacionDesc(Long mascotaId,Long veterinariaId);

    Optional<Desparasitaciones> findByIdAndVeterinariaId(Long desparasitacionId, Long veterinariaId);
}
