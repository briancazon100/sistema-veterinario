package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Desparasitaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DesparasitacionesRepository extends JpaRepository<Desparasitaciones, Long> {

    //listar todas las desparasitaciones del animal(id) en forma desc, de manera que el profesional pueda ver la ultima que se
    // le aplicó
    List<Desparasitaciones> findByMascotaIdOrderByFechaAplicacionDesc(Long mascotaId);
}
