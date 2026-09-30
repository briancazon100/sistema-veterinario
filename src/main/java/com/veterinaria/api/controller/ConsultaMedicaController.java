package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.consultaMedicaDto.ConsultaMedicaRequestDto;
import com.veterinaria.api.dtos.consultaMedicaDto.ConsultaMedicaResponseDto;
import com.veterinaria.api.service.ConsultaMedicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consultas-medicas")
@RequiredArgsConstructor
public class ConsultaMedicaController {

    private final ConsultaMedicaService consultaMedicaService;


    //endpoint para crear una nueva consulta
    @PostMapping
    public ResponseEntity<ConsultaMedicaResponseDto> save (@Valid @RequestBody ConsultaMedicaRequestDto consultaMedicaRequestDto){
        return new ResponseEntity<>(consultaMedicaService.save( consultaMedicaRequestDto),HttpStatus.CREATED);
    }

    //endpoint para buscar una consultaa por id
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaMedicaResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(consultaMedicaService.findById(id));
    }


    //endpoint para listar todas las consultas de una mascota por su id
    @GetMapping("/mascotas/{mascota_id}")
    public ResponseEntity<List<ConsultaMedicaResponseDto>> listarHistorialPorMascotaId (@PathVariable Long mascota_id){
        return  ResponseEntity.ok(consultaMedicaService.findByMascotaId(mascota_id));
    }
}
