package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.ClienteRequestDto;
import com.veterinaria.api.dtos.ClienteResponseDto;
import com.veterinaria.api.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes") // Versionar la API (v1) es otra buena práctica
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;  //gracias al requiredargsconstructor no debemos codear el constructor


    //endpoint para crear / guardar un cliente
    @PostMapping
    public ResponseEntity<ClienteResponseDto> save(@Valid @RequestBody ClienteRequestDto clienteRequestDto){//@Valid ejecuta las validaciones del DTO
        ClienteResponseDto respuesta= clienteService.save(clienteRequestDto); //le asignamos al service y le mandamos el cliente dto , luego la respuesta la guardamos en un dto response
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED); // Devuelve HTTP 201
    }


    //endpoint para listar todos los clientes
    @GetMapping
    public ResponseEntity<List<ClienteResponseDto>> findAll(){
        List<ClienteResponseDto> clientes=clienteService.findAll();
        return ResponseEntity.ok(clientes); // Devuelve HTTP 200 con la lista en JSON
    }


    //endpoint para obtener un cliente por su id
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> findById(@PathVariable Long id){
        ClienteResponseDto cliente= clienteService.findById(id);
        return ResponseEntity.ok(cliente); //devuelve http 200 con el cliente en JSON
    }


    //endpoint para actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> update(@PathVariable Long id, @Valid @RequestBody ClienteRequestDto clienteRequestDto){
        ClienteResponseDto clienteActualizado= clienteService.update(id,clienteRequestDto );

        return ResponseEntity.ok(clienteActualizado); // Devuelve HTTP 200 OK con el objeto actualizado

    }



    //endpoint para eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<ClienteResponseDto> delete(@PathVariable Long id){
        clienteService.delete(id);
        return ResponseEntity.noContent().build(); // Devuelve HTTP 204 No Content
    }
    }

