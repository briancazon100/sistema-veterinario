package com.veterinaria.api.service;

import com.veterinaria.api.dtos.clienteDto.ClienteRequestDto;
import com.veterinaria.api.dtos.clienteDto.ClienteResponseDto;
import com.veterinaria.api.entity.Cliente;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Inyecta el repositorio automáticamente por constructor
public class ClienteService {

    private final ClienteRepository clienteRepository;


    //metodo para guardar cliente
    public ClienteResponseDto save(ClienteRequestDto clienteRequestDto) {

        //1 regla de negocio: verificar si el telefono ya existe
        if (clienteRepository.existsByTelefono(clienteRequestDto.telefono())) {
            // Lanzamos BadRequestException
            throw new BadRequestException("El teléfono '" + clienteRequestDto.telefono() + "' ya está registrado");
        }

        // 2. Mapear DTO a Entidad , asi le enviamos a la db
        Cliente cliente = Cliente.builder()
                .nombre(clienteRequestDto.nombre())
                .apellido(clienteRequestDto.apellido())
                .telefono(clienteRequestDto.telefono())
                .direccion(clienteRequestDto.direccion())
                .build();

        //3. guardar en Base De Datos
        Cliente clienteGuardado = clienteRepository.save(cliente);

        // 4. Mapear Entidad a DTO para responder
        return new ClienteResponseDto(
                clienteGuardado.getId(),
                clienteGuardado.getNombre(),
                clienteGuardado.getApellido(),
                clienteGuardado.getTelefono(),
                clienteGuardado.getDireccion()
        );

    }

    //metodo para listar todos los clientes
    public List<ClienteResponseDto> findAll() {
        // Obtenemos todos los clientes de la BD y los mapeamos a DTO usando Streams
        return clienteRepository.findAll().stream()
                .map(cliente -> new ClienteResponseDto(
                        cliente.getId(),
                        cliente.getNombre(),
                        cliente.getApellido(),
                        cliente.getTelefono(),
                        cliente.getDireccion()
                ))
                .toList();
    }


    //metodo para buscar un cliente en particular
    public ClienteResponseDto findById(Long id) {
        // Buscamos por ID. Si no existe, lanzamos una excepción inmediatamente.
        //// Lanzamos ResourceNotFoundException
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        // Mapeamos a DTO y retornamos
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getDireccion()
        );
    }


    //metodo para actualizar un cliente
    public ClienteResponseDto update(Long id, ClienteRequestDto clienteRequestDto) {
        //1. validar que exista ese cliente
        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado al cliente con id " + id));

        //2. verificar que si cambió de teléfono, que no se le permita cambiarlo por el de otro cliente
        if (!cliente.getApellido().equals(clienteRequestDto.telefono()) && clienteRepository.existsByTelefono(clienteRequestDto.telefono())) {

            throw new BadRequestException("El teléfono " + clienteRequestDto.telefono() + " ya está registrado por otro cliente");
        }


        //3. actualizar los campos
        cliente.setNombre(clienteRequestDto.nombre());
        cliente.setApellido(clienteRequestDto.apellido());
        cliente.setTelefono(clienteRequestDto.telefono());
        cliente.setDireccion(clienteRequestDto.direccion());

        //4. guardar cambios en la DB
        Cliente clienteActualizado = clienteRepository.save(cliente);


        // 5. Mapear y retornar DTO
        return new ClienteResponseDto(
                clienteActualizado.getId(),
                clienteActualizado.getNombre(),
                clienteActualizado.getApellido(),
                clienteActualizado.getTelefono(),
                clienteActualizado.getDireccion()
        );
    }


    //metodo para eliminar cliente
    public void delete(Long id) {
        //verificar primeramente que exista ese cliente en la db
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontró al cliente con id " + id);
        }

        //borrar de la db
        clienteRepository.deleteById(id);
    }

}
