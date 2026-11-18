package com.veterinaria.api.service;

import com.veterinaria.api.dtos.mascotaDto.MascotaRequestDto;
import com.veterinaria.api.dtos.mascotaDto.MascotaResponseDto;
import com.veterinaria.api.entity.Cliente;
import com.veterinaria.api.entity.Mascota;
import com.veterinaria.api.entity.Veterinaria;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.ClienteRepository;
import com.veterinaria.api.repository.MascotaRepository;
import com.veterinaria.api.repository.VeterinariaRepository;
import com.veterinaria.api.security.TenantContext;
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
    private final VeterinariaRepository veterinariaRepository;

    //metodo para guardar mascota
    public MascotaResponseDto save(MascotaRequestDto mascotaRequestDto){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        Veterinaria veterinaria= veterinariaRepository.findById(veterinariaId).orElseThrow(()-> new ResourceNotFoundException(
                "No se encotró a la veterinaria con id  "+veterinariaId
        ));

        //1. regla de negocio: validar si existe eese dueño
        Cliente cliente= clienteRepository.findByIdAndVeterinariaId(mascotaRequestDto.clienteId(),veterinariaId).orElseThrow(()->
                new ResourceNotFoundException("No se encontró al cliente(dueño) con id"+mascotaRequestDto.clienteId() ));

        // 2. Mapear DTO a Entity
        Mascota nuevaMascota = Mascota.builder()
                .nombre(mascotaRequestDto.nombre())
                .especie(mascotaRequestDto.especie())
                .raza(mascotaRequestDto.raza())
                .fecha_nacimiento(mascotaRequestDto.fechaNacimiento())
                .cliente(cliente)
                .estado(true)
                .veterinaria(veterinaria)
                .build();

        //3. guardar en la db
        Mascota mascotaGuardada= mascotaRepository.save(nuevaMascota);

        // 4. Retornar DTO
        return mapToDTO(mascotaGuardada);
    }



    //metodo para listar todas las mascotas activas
    public List<MascotaResponseDto> findAllActivas(){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        return mascotaRepository.findByEstadoTrueAndVeterinariaId(veterinariaId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<MascotaResponseDto> findAll(){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        return mascotaRepository.findByVeterinariaId(veterinariaId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    //metodo para buscar por id una mascota activa
    public MascotaResponseDto findById(Long id){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        Mascota mascota= mascotaRepository.findByIdAndEstadoTrueAndVeterinariaId(id, veterinariaId).orElseThrow(()->
                new ResourceNotFoundException("No se encontró la mascota con id "+id));
        return mapToDTO(mascota);
    }

    //buscar todas las mascotas que tiene un cliente por cliente_id
    public List<MascotaResponseDto> findByClienteId(Long clienteId){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //validar primero si existe ese cliente
        if(!clienteRepository.existsByIdAndVeterinariaId(clienteId, veterinariaId)){
            throw new ResourceNotFoundException("No se encuentra registrado el cliente con id "+clienteId);
        }

        return mascotaRepository.findByClienteIdAndVeterinariaId(clienteId, veterinariaId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    //actualizar una mascota
    public MascotaResponseDto update(Long id, MascotaRequestDto mascotaRequestDto){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //validar que exista esa mascota
        Mascota mascota=mascotaRepository.findByIdAndEstadoTrueAndVeterinariaId(id,veterinariaId).orElseThrow(()->
                new ResourceNotFoundException("No se encuentra registrada la mascota con id "+id));

        //validar que si cambió de dueño, que el nuevo cliente exista
        if(!mascota.getCliente().getId().equals(mascotaRequestDto.clienteId())){
            Cliente nuevoCliente= clienteRepository.findByIdAndVeterinariaId(mascotaRequestDto.clienteId(),
                    veterinariaId).orElseThrow(()->
                    new ResourceNotFoundException("No se encontró el cliente con id "+mascotaRequestDto.clienteId()));
            mascota.setCliente(nuevoCliente);
        }

        //pasar de dto a entidad para poder enviarle a la base de datos
        mascota.setNombre(mascotaRequestDto.nombre());
        mascota.setEspecie(mascotaRequestDto.especie());
        mascota.setRaza(mascotaRequestDto.raza());
        mascota.setFecha_nacimiento(mascotaRequestDto.fechaNacimiento());

        //guardar en la db
        Mascota actualizada= mascotaRepository.save(mascota);


        //mapear a deto para responder
        return mapToDTO(actualizada);
    }


    //metodo para eliminar
    public void delete(Long id){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //validar que exista la mascota
       Mascota mascota= mascotaRepository.findByIdAndEstadoTrueAndVeterinariaId(id,veterinariaId ).
               orElseThrow(()->new ResourceNotFoundException("No se econtró a la mascota con id "+id));

        //borrado logico
        mascota.setEstado(false);

        //guardar en la db el nuevo estado de la mascota, osea esta borrado pero permanece en la db
        mascotaRepository.save(mascota);
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
                nombreCliente,
                mascota.isEstado()
        );
    }

}
