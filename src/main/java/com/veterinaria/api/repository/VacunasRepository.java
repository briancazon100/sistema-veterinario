package com.veterinaria.api.repository;

import com.veterinaria.api.entity.RegistroVacunas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VacunasRepository extends JpaRepository<RegistroVacunas, Long> {

    //metodo para listar todas la vacunas de un animal de forma desc (de los mas nuevos a los viejos)
    List<RegistroVacunas> findByMascotaIdOrderByFechaAplicacionDesc(Long mascota_id);
}
