package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.mascotaDto.MascotaRequestDto;
import com.veterinaria.api.dtos.mascotaDto.MascotaResponseDto;
import com.veterinaria.api.service.MascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;


    //endpoint para crear mascota
    @PostMapping
    public ResponseEntity<MascotaResponseDto> save(@Valid @RequestBody MascotaRequestDto mascotaRequestDto){
        return new ResponseEntity<>(mascotaService.save(mascotaRequestDto), HttpStatus.CREATED);
    }

    //endpoint para listar todas las mascotas
    @GetMapping
    public ResponseEntity<List<MascotaResponseDto>> findAll(){
        return ResponseEntity.ok(mascotaService.findAll());
    }


    //endpoint para buscar por id una mascota
    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(mascotaService.findById(id));
    }

    //endpoint especializado: buscar todas las mascotas de un cliente(dueño)}
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<MascotaResponseDto>> findByCliente(@PathVariable Long id){
        return ResponseEntity.ok(mascotaService.findByClienteId(id));
    }

    //endpoint para actualizar una mascota
    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponseDto> update(@PathVariable Long id, @Valid @RequestBody MascotaRequestDto mascotaRequestDto){
        return  ResponseEntity.ok(mascotaService.update(id, mascotaRequestDto));
    }

    //endpoint para eliminar unas mascota
    @DeleteMapping("/{id}")
    public ResponseEntity<MascotaResponseDto> delete(@PathVariable Long id){
        mascotaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //endpoint para buscar mascota por nombre
    @GetMapping("/nombre")
    public ResponseEntity<List<MascotaResponseDto>> findByName(@RequestParam String nombre){
        List<MascotaResponseDto> mascotas=mascotaService.findByName(nombre);
        return ResponseEntity.ok(mascotas);
    }



}
