package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Turno;
import com.veterinaria.api.entity.enums.EstadoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    //listar todos los turnos pendientes del dia corriente de un veterinario ordenados de los tempranos a los de mas tarde
    List<Turno> findByVeterinarioIdAndEstadoAndFechaHoraBetweenOrderByFechaHoraAsc(Long veterinarioId, EstadoTurno estado,
                                                                                   LocalDateTime inicioDia, LocalDateTime finDia);

}
