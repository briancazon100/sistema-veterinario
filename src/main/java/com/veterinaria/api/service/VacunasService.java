package com.veterinaria.api.service;

import com.veterinaria.api.dtos.consultaMedicaDto.ConsultaMedicaResponseDto;
import com.veterinaria.api.dtos.vacunasDto.VacunasRequestDto;
import com.veterinaria.api.dtos.vacunasDto.VacunasResponseDto;
import com.veterinaria.api.entity.ConsultaMedica;
import com.veterinaria.api.entity.Mascota;
import com.veterinaria.api.entity.RegistroVacunas;
import com.veterinaria.api.entity.Veterinario;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.MascotaRepository;
import com.veterinaria.api.repository.VacunasRepository;
import com.veterinaria.api.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacunasService {

    private final VacunasRepository vacunasRepository; //inyeccion de dependencias sin constructor codeeado gracias a @required

    private final MascotaRepository mascotaRepository;

    private final VeterinarioRepository veterinarioRepository;



    //metodo para guardar un registro de vacuna
    @Transactional // <-- Para asegurar que se guarde todo o se deshaga si hay error
    public VacunasResponseDto save(VacunasRequestDto vacunasRequestDto){
        // validar si existe el veterinario y la mascota
        // en caso de que lo encuentre lo guarda en el objeto, si no, lanza inmediatamente la excepción
        Mascota mascota=  mascotaRepository.findById(vacunasRequestDto.mascotaId()).orElseThrow(()->
                new ResourceNotFoundException("No se encontró a la mascota con id "+vacunasRequestDto.mascotaId()));

        Veterinario veterinario = veterinarioRepository.findById(vacunasRequestDto.veterinarioId()).orElseThrow(()->
                new ResourceNotFoundException("No se encontró al veterinario con id  "+vacunasRequestDto.veterinarioId()));

        //validar que la proxima fecha de aplicacion no sea anterior a la fecha de la vacuna aplicada recientemente
        if(vacunasRequestDto.fechaProximaDosis().isBefore(vacunasRequestDto.fechaAplicacion())){
            throw new BadRequestException("La fecha de la próxima dosis debe ser posterior a la aplicada recientemente");
        }


        //  pasar de dto a entidad para luego poder mandarle a la base de datos, y asignar fecha actual
        RegistroVacunas registroVacunas = RegistroVacunas.builder()
                .nombreVacuna(vacunasRequestDto.nombreVacuna())
                .fechaAplicacion(vacunasRequestDto.fechaAplicacion())
                .fechaProximaDosis(vacunasRequestDto.fechaProximaDosis())
                .mascota(mascota)
                .veterinario(veterinario)
                .build();

        //pasar a la base de datos
        RegistroVacunas registroVacunasGuardada=vacunasRepository.save(registroVacunas);

        // pasar a dto y retornar
        return mapToDTO(registroVacunasGuardada);


    }



    // metodo para listar todas los registros de vacunas de un animal(por su id)
    @Transactional(readOnly = true) // <-- Para optimizar la lectura
    public List<VacunasResponseDto> verHistorialVacunas(Long id){
        if(!mascotaRepository.existsById(id)){
            throw  new ResourceNotFoundException("No se encontró a la mascota con id "+id);
        }

        // buscar en la db la lista de todas la consultas de una mascota y luego a eso pasarlo a dto y retonar, algo mas directp
        return vacunasRepository.findByMascotaIdOrderByFechaAplicacionDesc(id)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

    }







    public VacunasResponseDto mapToDTO(RegistroVacunas registroVacunas) {
        return new VacunasResponseDto(
                registroVacunas.getId(),
                registroVacunas.getNombreVacuna(),
                registroVacunas.getFechaAplicacion(),
                registroVacunas.getFechaProximaDosis(),
                registroVacunas.getVeterinario().getId(),
                registroVacunas.getMascota().getId()

        );
    }






}
