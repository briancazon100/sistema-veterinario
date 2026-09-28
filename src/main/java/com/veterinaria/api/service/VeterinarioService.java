package com.veterinaria.api.service;

import com.veterinaria.api.dtos.clienteDto.ClienteResponseDto;
import com.veterinaria.api.dtos.mascotaDto.MascotaResponseDto;
import com.veterinaria.api.dtos.veterinarioDto.VeterinarioRequestDto;
import com.veterinaria.api.dtos.veterinarioDto.VeterinarioResponseDto;
import com.veterinaria.api.entity.Cliente;
import com.veterinaria.api.entity.Mascota;
import com.veterinaria.api.entity.Veterinario;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Inyecta el repositorio automáticamente por constructor
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;


    //metodos

    //metodo para guardar un veterinario
    public VeterinarioResponseDto save(VeterinarioRequestDto veterinarioRequestDto){
        //1.regla de nogocio: validar que la matricula no se repita con el de otro veterinario
        if(veterinarioRepository.existsByMatricula(veterinarioRequestDto.matricula())){
            // Lanzamos BadRequestException
            throw new BadRequestException("Ya se encuentra registrado un veterinario con la matrícula: "+veterinarioRequestDto.matricula());
        }

        //2.mapear de dto a entidad para enviarle a la base de datos   y que lo guarde
        Veterinario veterinario = Veterinario.builder()
                .nombre(veterinarioRequestDto.nombre())
                .apellido(veterinarioRequestDto.apellido())
                .matricula(veterinarioRequestDto.matricula())
                .especialidad(veterinarioRequestDto.especialidad())
                .build();

        //3. guardar en la base de datos
        Veterinario veterinarioGuardado=veterinarioRepository.save(veterinario);

        //4. devolver el dto para que el controller pueda tambien retornarselo al cliente
        return mapToDTO(veterinarioGuardado);

    }

    //metodo para buscar por id
    public VeterinarioResponseDto findById(Long id){
        //1. regla de negocio: validar si existe el veterinario
        // Buscamos por ID. Si no existe, lanzamos una excepción inmediatamente.
        //// Lanzamos ResourceNotFoundException
        Veterinario veterinario= veterinarioRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(
                "No se encontró al veterinario con id :"+id));

        //devoler en dto el veterinario encontrado
        return mapToDTO(veterinario);
    }



    //metodo para listar todos los veterinarios
    public List<VeterinarioResponseDto> findAll(){
        // Obtenemos todos los veterinarios de la BD y los mapeamos a DTO usando Streams
        return veterinarioRepository.findAll().stream()
                .map(veterinario -> new VeterinarioResponseDto(
                        veterinario.getId(),
                        veterinario.getNombre(),
                        veterinario.getApellido(),
                        veterinario.getMatricula(),
                        veterinario.getEspecialidad()
                ))
                .toList();
    }




    //metodo para actualizar un veterinario
    public VeterinarioResponseDto update(Long id, VeterinarioRequestDto veterinarioRequestDto){
        //1 regla de negocio: validar si existe ese veterinario
        Veterinario veterinario=veterinarioRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException(
                "No se encontró el veterinario con id "+id));

        //2. actualizar los datos : del veterinario que encontramos, le actualizamos (seteamos) los datos con lo que viene
        //del dto
        veterinario.setNombre(veterinarioRequestDto.nombre());
        veterinario.setApellido(veterinarioRequestDto.apellido());
        veterinario.setMatricula(veterinarioRequestDto.matricula());
        veterinario.setEspecialidad(veterinarioRequestDto.especialidad());

        //3. actualizar en la base de datos al veterinario con sus nuevos datos
        Veterinario  veterinarioActualizado=veterinarioRepository.save(veterinario);

        //4. Mapear y retornar DTO
        return mapToDTO(veterinarioActualizado);
    }



    // Método helper para no repetir el mapeo a DTO
    private VeterinarioResponseDto mapToDTO(Veterinario veterinario) {

        return new VeterinarioResponseDto(
                veterinario.getId(),
                veterinario.getNombre(),
                veterinario.getApellido(),
                veterinario.getMatricula(),
                veterinario.getEspecialidad()
        );
    }

}
