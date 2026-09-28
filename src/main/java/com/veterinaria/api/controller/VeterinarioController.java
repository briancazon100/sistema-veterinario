package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.veterinarioDto.VeterinarioRequestDto;
import com.veterinaria.api.dtos.veterinarioDto.VeterinarioResponseDto;
import com.veterinaria.api.entity.Veterinario;
import com.veterinaria.api.service.VeterinarioService;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/veterinarios")
@RequiredArgsConstructor // Inyecta el repositorio automáticamente por constructor
public class VeterinarioController {

    private final VeterinarioService veterinarioService;


    //endpoint para guardar un veterinario
    @PostMapping
    public ResponseEntity<VeterinarioResponseDto> save(@Valid @RequestBody VeterinarioRequestDto veterinarioRequestDto){
        return new ResponseEntity<>(veterinarioService.save(veterinarioRequestDto), HttpStatus.CREATED);
    }


    //endpoint para obtener un veterinario por su id
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(veterinarioService.findById(id));
    }


    //enpoint para listar todos los veterinarios
    @GetMapping
    public ResponseEntity<List<VeterinarioResponseDto>> findAll(){
        return ResponseEntity.ok(veterinarioService.findAll());
    }


    //endpoint para actualizar un veterinario
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponseDto> update (@PathVariable Long id, @Valid @RequestBody VeterinarioRequestDto veterinarioRequestDto){
        return ResponseEntity.ok(veterinarioService.update(id, veterinarioRequestDto));
    }
}
