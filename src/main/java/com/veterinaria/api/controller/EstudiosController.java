package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.estudiosComplemenetariosDto.EstudiosRequestDto;
import com.veterinaria.api.dtos.estudiosComplemenetariosDto.EstudiosResponseDto;
import com.veterinaria.api.dtos.estudiosComplemenetariosDto.ResultadoEstudiosDto;
import com.veterinaria.api.service.EstudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/estudios")
@RequiredArgsConstructor
public class EstudiosController {

    private final EstudioService estudioService;

    //endpoint para crear una nueva orden de estudios
    @PostMapping
    public ResponseEntity<EstudiosResponseDto> crearOrden (@Valid @RequestBody EstudiosRequestDto estudiosRequestDto){
        return new ResponseEntity<>(estudioService.crearOrden(estudiosRequestDto), HttpStatus.CREATED);
    }

    //endpoint para cargar el resultado y / o   el archivo
    @PatchMapping(value = "/{idEstudio}/resultado", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EstudiosResponseDto> cargarResultado(
            @PathVariable Long idEstudio,
            @ModelAttribute @Valid ResultadoEstudiosDto dto,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) throws IOException {

        return ResponseEntity.ok(estudioService.cargarResultado(idEstudio, dto, archivo));

    }


    //endpoint para cancelar una orden
    @PatchMapping("/{idEstudio}")
    public ResponseEntity<EstudiosResponseDto> cancelarOrden(@PathVariable Long idEstudio){
        return  ResponseEntity.ok(estudioService.cancelarOrden(idEstudio));
    }



    //endpoint para listar todos los estudios realizados de una consulta
    @GetMapping("/{idConsulta}/consultaMedica")
    public ResponseEntity<List<EstudiosResponseDto>> consultarEstudiosPorConsulta(@PathVariable Long idConsulta){
        return  ResponseEntity.ok(estudioService.verEstudiosPorConsultaId(idConsulta));
    }


    //endpoint para listar todo el historial de todos los estudios realizados de una mascota desde el mas reciente
    @GetMapping("/{idMascota}/mascotas")
    public ResponseEntity<List<EstudiosResponseDto>> verHistorialEstudiosMascota(@PathVariable Long idMascota){
        return ResponseEntity.ok(estudioService.verHistorialEstudios(idMascota));
    }


}
