package com.veterinaria.api.service;

import com.veterinaria.api.dtos.desparasitaciones.DesparasitacionesResponseDto;
import com.veterinaria.api.dtos.turnoDto.TurnoEstadoRequestDto;
import com.veterinaria.api.dtos.turnoDto.TurnoRequestDto;
import com.veterinaria.api.dtos.turnoDto.TurnoResponseDto;
import com.veterinaria.api.entity.Desparasitaciones;
import com.veterinaria.api.entity.Mascota;
import com.veterinaria.api.entity.Turno;
import com.veterinaria.api.entity.Veterinario;
import com.veterinaria.api.entity.enums.EstadoTurno;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.MascotaRepository;
import com.veterinaria.api.repository.TurnoRepository;
import com.veterinaria.api.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TurnoService {

    private final TurnoRepository turnoRepository;

    private final MascotaRepository mascotaRepository;

    private final VeterinarioRepository veterinarioRepository;

    //metodo para crear un nuevo turno
    @Transactional
    public TurnoResponseDto save (TurnoRequestDto turnoRequestDto){
        //validar que la mascota y el veterinario existan
        Mascota mascota= mascotaRepository.findById(turnoRequestDto.mascotaId()).orElseThrow(()-> new ResourceNotFoundException(
                "La mascota con id "+turnoRequestDto.mascotaId()+" no existe"
        ));

        Veterinario veterinario=veterinarioRepository.findById(turnoRequestDto.veterinarioId()).orElseThrow(()->
                new ResourceNotFoundException("El veterinario con id "+turnoRequestDto.veterinarioId()+" no existe"));

        //validar que no saque un turno que sea anterior  ya sea al dia o a la hora exacta corriente
        if(turnoRequestDto.fechaHora().isBefore(LocalDateTime.now())){
            throw  new BadRequestException("El turno debe ser posterior al dia u hora corriente");
        }


        // Mapear DTO a Entity para poder luego mandarle a la db
        Turno turno = Turno.builder()
                .fechaHora(turnoRequestDto.fechaHora())
                .motivo(turnoRequestDto.motivo())
                .estado(EstadoTurno.PENDIENTE)
                .mascota(mascota)
                .veterinario(veterinario)
                .build();

        //pasar a la db
        Turno turnoGuardado=turnoRepository.save(turno);

        //mepear a dto y retornar
        return mapToDTO(turnoGuardado);


    }

    //metodo para consultar los turnos pendientes del dia corriente de un veterinario
    public List<TurnoResponseDto> consultarTurnosPendientesHoy(Long veterinarioId){
        //validar si existe el veterinario
        if (!veterinarioRepository.existsById(veterinarioId)){
            throw new ResourceNotFoundException(("No existe el veterinario con id "+veterinarioId));
        }

        // Calcular los límites del día de hoy
        LocalDate hoy = LocalDate.now(); // Ej: 2026-10-02
        LocalDateTime inicioDia = hoy.atStartOfDay(); // 2026-10-02 00:00:00
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX); // 2026-10-02 23:59:59.999999999

        //consultar en la base de datos
        List<Turno> turnosHoy=turnoRepository.findByVeterinarioIdAndEstadoAndFechaHoraBetweenOrderByFechaHoraAsc(veterinarioId,EstadoTurno.PENDIENTE
        ,inicioDia,finDia);

        // Convertir a DTO y devolver
        return turnosHoy.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }




    //metodo para cambiar el estado de un turno(cancelado o atendido)
    @Transactional //ponemos esto porque vamos a cambiar un registro en la db, no solo lo vamos a leer
    public TurnoResponseDto updateEstado(Long turnoId  ,  TurnoEstadoRequestDto turnoEstadoRequestDto){
        // 1. Buscar el turno
        Turno turno= turnoRepository.findById(turnoId).orElseThrow(()->new ResourceNotFoundException(
                "No se encontró el turno con id "+turnoId
        ));

        //verificamos que si el turno ya tiene estado atendido, que no lo pueda cambiar
        if(turno.getEstado().equals(EstadoTurno.ATENDIDO)){
            throw  new BadRequestException("No se puede cambiar un turno que ya fue atendido");
        }

        //actualizar solo el estado del turno
        turno.setEstado(turnoEstadoRequestDto.estadoTurno());

        //mandar a la base de datos pa que actualize
        Turno turnoActualizado=turnoRepository.save(turno);

        //mapear de entity a dto para poder retornar
        return mapToDTO(turnoActualizado);

    }



    // Método helper para no repetir el mapeo de ENtity a DTO
    public TurnoResponseDto mapToDTO(Turno turno) {

        return new TurnoResponseDto(
                turno.getId(),
                turno.getFechaHora(),
                turno.getMotivo(),
                turno.getEstado(),
                turno.getMascota().getId(),
                turno.getVeterinario().getId()
        );
    }
}
