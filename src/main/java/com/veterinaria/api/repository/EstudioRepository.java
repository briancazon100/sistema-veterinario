package com.veterinaria.api.repository;

import com.veterinaria.api.entity.EstudioComplementario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstudioRepository extends JpaRepository<EstudioComplementario, Long> {

    //listar los estudios de un consulta
    List<EstudioComplementario> findByConsultaMedicaId(Long consultaId);

    //listar el historial de estudios de una mascota oredenados desde el más reciente
    List<EstudioComplementario> findByConsultaMedicaMascotaIdOrderByFechaRealizacionDesc(Long mascotaId);
}
