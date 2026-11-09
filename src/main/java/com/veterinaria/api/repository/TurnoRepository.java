package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Turno;
import com.veterinaria.api.entity.enums.EstadoTurno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    //listar todos los turnos pendientes del dia corriente de un veterinario ordenados de los tempranos a los de mas tarde
    List<Turno> findByVeterinarioIdAndEstadoAndFechaHoraBetweenOrderByFechaHoraAsc(Long veterinarioId, EstadoTurno estado,
                                                                                   LocalDateTime inicioDia, LocalDateTime finDia);


    //listar todos los turnos pendientes desde hoy hacia el futuro
    List<Turno> findByEstadoAndFechaHoraAfterOrderByFechaHoraAsc(EstadoTurno estado, LocalDateTime hoy);


    //listar todos los turnos que sean anteriores a hoy que esten en estado pendiente
    List<Turno> findByEstadoAndFechaHoraBefore(EstadoTurno estado, LocalDateTime fechaHoy);

    // Forzamos explícitamente el ordenamiento en el JPQL
    @Query("SELECT t FROM Turno t ORDER BY t.fechaHora DESC")
    Page<Turno> findByOrderByFechaHoraDesc(Pageable pageable);
}
