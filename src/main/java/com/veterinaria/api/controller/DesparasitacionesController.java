package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.desparasitaciones.DesparasitacionesRequestDto;
import com.veterinaria.api.dtos.desparasitaciones.DesparasitacionesResponseDto;
import com.veterinaria.api.service.DesparasitacionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/desparasitaciones")
@RequiredArgsConstructor
public class DesparasitacionesController {

    private final DesparasitacionesService desparasitacionesService;


    //endpoint para guardar un registro de desparasitacion
    @PostMapping
    public ResponseEntity<DesparasitacionesResponseDto> save (@Valid @RequestBody DesparasitacionesRequestDto desparasitacionesRequestDto){
        return new ResponseEntity<>(desparasitacionesService.save(desparasitacionesRequestDto),HttpStatus.CREATED);
    }


    //endpoint para ver historial de desparasitaciones de una mascota(id)
    @GetMapping("/{mascotaId}")
    public ResponseEntity<List<DesparasitacionesResponseDto>> verHistorialDesparacitaciones(@PathVariable Long mascotaId){
        return ResponseEntity.ok(desparasitacionesService.verHistorialDesparasitaciones(mascotaId));
    }
}
