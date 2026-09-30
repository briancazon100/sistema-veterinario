package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.vacunasDto.VacunasRequestDto;
import com.veterinaria.api.dtos.vacunasDto.VacunasResponseDto;
import com.veterinaria.api.service.VacunasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacunas")
@RequiredArgsConstructor
public class VacunasController {

    private final VacunasService vacunasService;


    //endpoint para crear/ guardar un registro de vacunas
    @PostMapping
    public ResponseEntity<VacunasResponseDto> save (@Valid @RequestBody VacunasRequestDto vacunasRequestDto){
        return new ResponseEntity<>(vacunasService.save(vacunasRequestDto), HttpStatus.CREATED);
    }



    //endpoint para listar todas los registros de vacuna de un animal por su id
    @GetMapping("/{id}")
    public ResponseEntity<List<VacunasResponseDto>> verHistorialVacunas(@PathVariable Long id){
        return ResponseEntity.ok(vacunasService.verHistorialVacunas(id));
    }

}
