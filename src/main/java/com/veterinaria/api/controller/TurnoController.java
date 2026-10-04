package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.turnoDto.TurnoEstadoRequestDto;
import com.veterinaria.api.dtos.turnoDto.TurnoRequestDto;
import com.veterinaria.api.dtos.turnoDto.TurnoResponseDto;
import com.veterinaria.api.entity.enums.EstadoTurno;
import com.veterinaria.api.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    //endpoint para guardar un turno
    @PostMapping
    public ResponseEntity<TurnoResponseDto> save (@Valid @RequestBody TurnoRequestDto turnoRequestDto){
        return new ResponseEntity<>(turnoService.save(turnoRequestDto), HttpStatus.CREATED);
    }


    //endpoint para consultar todos los turnos  pendientes de hoy de un veterinario
    @GetMapping("/{veterinarioId}")
    public ResponseEntity<List<TurnoResponseDto>> verTurnosPendientesHoy(@PathVariable Long veterinarioId){
        return ResponseEntity.ok(turnoService.consultarTurnosPendientesHoy(veterinarioId));
    }



    //endpoint para actualizar el estado de un turno
    @PatchMapping("/{idTurno}/estado")
    public ResponseEntity<TurnoResponseDto> updateEstado(@PathVariable Long idTurno, @Valid @RequestBody TurnoEstadoRequestDto estado){
        return ResponseEntity.ok(turnoService.updateEstado(idTurno, estado));

    }
}
