package com.veterinaria.api.service;

import com.veterinaria.api.dtos.desparasitaciones.DesparasitacionesRequestDto;
import com.veterinaria.api.dtos.desparasitaciones.DesparasitacionesResponseDto;
import com.veterinaria.api.dtos.mascotaDto.MascotaResponseDto;
import com.veterinaria.api.entity.Desparasitaciones;
import com.veterinaria.api.entity.Mascota;
import com.veterinaria.api.entity.Veterinaria;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.DesparasitacionesRepository;
import com.veterinaria.api.repository.MascotaRepository;
import com.veterinaria.api.repository.VeterinariaRepository;
import com.veterinaria.api.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 1. Regla por defecto: TODA la clase es de "solo lectura"
//osea que todos los metodos salvo los que tengan explicitamente el transactional solo estaran bajo la optimizacion de lectura
//en la db, las excepciones son los metodos que hagan cambios en la db
//con esto nos aseguramos basicamente que todo ande bien, por ej si corta el internet en pleno insert se cancela todo
public class DesparasitacionesService {

    private final DesparasitacionesRepository desparasitacionesRepository;

    private final MascotaRepository mascotaRepository;

    private final VeterinariaRepository veterinariaRepository;

    //metodo para crear un nuevo registro de desparasitacion
    //aca incluimos Transactional porque no vamos a leer la db, vamos a cambiar/crear un registro
    @Transactional
    public DesparasitacionesResponseDto save (DesparasitacionesRequestDto desparasitacionesRequestDto){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        Veterinaria veterinaria= veterinariaRepository.findById(veterinariaId).orElseThrow(()->new ResourceNotFoundException(
                "No se encontró a la veterinaria con id "+veterinariaId
        ));


        //reglas de negocios: validar que la proxima dosis no sea antes que la dosis aplicada
        if(desparasitacionesRequestDto.fechaProximaDosis().isBefore(desparasitacionesRequestDto.fechaAplicacion())){
            throw  new BadRequestException("La próxima dósis debe ser posterior a la aplicada");
        }

        //validar que la fecha aplicada no sea posterior a hoy
        if(desparasitacionesRequestDto.fechaAplicacion().isAfter(LocalDate.now())){
            throw new BadRequestException("La fecha de aplicación no debe ser posterior a hoy");
        }

        //validar que la mascota exista
        Mascota mascota= mascotaRepository.findByIdAndEstadoTrueAndVeterinariaId(desparasitacionesRequestDto.mascotaId(), veterinariaId).orElseThrow(()->
                new ResourceNotFoundException("La mascota con id "+desparasitacionesRequestDto.mascotaId()+" no existe"));

        //pasar el dto que recibimos a entity para luego poder mandarle a la base de datos
        // Mapear DTO a Entity
        Desparasitaciones desparasitaciones = Desparasitaciones.builder()
                .productoUsado(desparasitacionesRequestDto.productoUsado())
                .fechaAplicacion(desparasitacionesRequestDto.fechaAplicacion())
                .fechaProximaDosis(desparasitacionesRequestDto.fechaProximaDosis())
                .mascota(mascota)
                .veterinaria(veterinaria)
                .build();

        //Guardar en la base de datos
        Desparasitaciones desparasitacionesGuardado=desparasitacionesRepository.save(desparasitaciones);


        //mepear de entity a dto y retornar
        return mapToDTO(desparasitacionesGuardado);


    }




    //metodo para listar todas las desparasitaciones de un animal en particular (id)
    //Este método ya no necesita anotación. Automáticamente hereda
    // el "readOnly = true" de la clase porque solo hace un SELECT.
    public List<DesparasitacionesResponseDto> verHistorialDesparasitaciones(Long mascotaId){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //validar si existe la mascota
        if(!mascotaRepository.existsByIdAndVeterinariaId(mascotaId, veterinariaId)){
            throw  new ResourceNotFoundException("No existe la mascota con id "+mascotaId);
        }


        return desparasitacionesRepository.findByMascotaIdOrderByFechaAplicacionDesc(mascotaId).stream()
                .map(this::mapToDTO)
                .toList();
    }





    // Método helper para no repetir el mapeo a DTO
    public DesparasitacionesResponseDto mapToDTO(Desparasitaciones desparacitaciones) {

        return new DesparasitacionesResponseDto(
                desparacitaciones.getId(),
                desparacitaciones.getProductoUsado(),
                desparacitaciones.getFechaAplicacion(),
                desparacitaciones.getFechaProximaDosis(),
                desparacitaciones.getMascota().getId()
        );
    }




}
