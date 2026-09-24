package com.veterinaria.api.service;

import com.veterinaria.api.dtos.mascotaDto.MascotaRequestDto;
import com.veterinaria.api.dtos.mascotaDto.MascotaResponseDto;
import com.veterinaria.api.entity.Cliente;
import com.veterinaria.api.entity.Mascota;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.ClienteRepository;
import com.veterinaria.api.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MascotaService {

    private final ClienteRepository clienteRepository;
    private final MascotaRepository mascotaRepository;

    //metodo para guardar mascota
    public MascotaResponseDto save(MascotaRequestDto mascotaRequestDto){
        //1. regla de negocio: validar si existe eese dueño
        Cliente cliente= clienteRepository.findById(mascotaRequestDto.clienteId()).orElseThrow(()->
                new ResourceNotFoundException("No se encontró al cliente(dueño) con id"+mascotaRequestDto.clienteId() ));

        // 2. Mapear DTO a Entity
        Mascota nuevaMascota = Mascota.builder()
                .nombre(mascotaRequestDto.nombre())
                .especie(mascotaRequestDto.especie())
                .raza(mascotaRequestDto.raza())
                .fecha_nacimiento(mascotaRequestDto.fecha_nacimiento())
                .cliente(cliente)
                .build();

        //3. guardar en la db
        Mascota mascotaGuardada= mascotaRepository.save(nuevaMascota);

        // 4. Retornar DTO
        return mapToDTO(mascotaGuardada);
    }



    //metodo para listar todas las mascotas
    public List<MascotaResponseDto> findAll(){
        return mascotaRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    //metodo para buscar por id una mascota
    public MascotaResponseDto findById(Long id){
        Mascota mascota= mascotaRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("No se encontró la mascota con id "+id));
        return mapToDTO(mascota);
    }

    //buscar todas las mascotas que tiene un cliente por cliente_id
    public List<MascotaResponseDto> findByClienteId(Long cliente_id){
        //validar primero si existe ese cliente
        if(!clienteRepository.existsById(cliente_id)){
            throw new BadRequestException("No se encuentra registrado el cliente con id "+cliente_id);
        }

        return mascotaRepository.findByClienteId(cliente_id).stream()
                .map(this::mapToDTO)
                .toList();
    }

    //actualizar una mascota
    public MascotaResponseDto update(Long id, MascotaRequestDto mascotaRequestDto){
        //validar que exista esa mascota
        Mascota mascota=mascotaRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("No se encuentra registrada la mascota con id "+id));

        //validar que si cambió de dueño, que el nuevo cliente exista
        if(!mascota.getCliente().getId().equals(mascotaRequestDto.clienteId())){
            Cliente nuevoCliente= clienteRepository.findById(mascotaRequestDto.clienteId()).orElseThrow(()->
                    new ResourceNotFoundException("No se encontró el cliente con id "+mascotaRequestDto.clienteId()));
            mascota.setCliente(nuevoCliente);
        }

        //pasar de dto a entidad para poder enviarle a la base de datos
        mascota.setNombre(mascotaRequestDto.nombre());
        mascota.setEspecie(mascotaRequestDto.especie());
        mascota.setRaza(mascotaRequestDto.raza());
        mascota.setFecha_nacimiento(mascotaRequestDto.fecha_nacimiento());

        //guardar en la db
        Mascota actualizada= mascotaRepository.save(mascota);


        //mapear a deto para responder
        return mapToDTO(actualizada);
    }


    //metodo para eliminar
    public void delete(Long id){
        //validar que exista la mascota
        if(!mascotaRepository.existsById(id)){
            throw  new ResourceNotFoundException("No se encontró una mascota con el id "+id);
        }

        //eliminar
        mascotaRepository.deleteById(id);
    }


    //metodo para buscar mascota por su nombre
    public List<MascotaResponseDto> findByName(String nombre){
        List<Mascota> mascotas=mascotaRepository.findByNombreContainingIgnoreCase(nombre);
        return mascotas.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    // Método helper para no repetir el mapeo a DTO
    private MascotaResponseDto mapToDTO(Mascota mascota) {
        String nombreCliente = mascota.getCliente().getNombre() + " " + mascota.getCliente().getApellido();
        return new MascotaResponseDto(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getFecha_nacimiento(),
                mascota.getCliente().getId(),
                nombreCliente
        );
    }

}
