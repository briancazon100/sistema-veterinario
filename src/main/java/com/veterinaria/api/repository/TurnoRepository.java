package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Turno;
import com.veterinaria.api.entity.enums.EstadoTurno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    //listar todos los turnos pendientes del dia corriente de un veterinario ordenados de los tempranos a los de mas tarde
    List<Turno> findByVeterinarioIdAndEstadoAndVeterinariaIdAndFechaHoraBetweenOrderByFechaHoraAsc(Long veterinarioId, EstadoTurno estado,Long veterinariaId,
                                                                                   LocalDateTime inicioDia, LocalDateTime finDia);


    //listar todos los turnos pendientes desde hoy hacia el futuro
    List<Turno> findByEstadoAndVeterinariaIdAndFechaHoraAfterOrderByFechaHoraAsc(EstadoTurno estado, Long veterinariaId, LocalDateTime hoy);


    //listar todos los turnos que sean anteriores a hoy que esten en estado pendiente
    List<Turno> findByEstadoAndFechaHoraBeforeAndVeterinariaId(EstadoTurno estado, LocalDateTime fechaHoy, Long veterinariaId);

  
    Page<Turno> findByVeterinariaIdOrderByFechaHoraDesc(Long veterinariaId, Pageable pageable);

    Optional<Turno> findByIdAndVeterinariaId(Long id, Long veterinariaId);
}
