package com.veterinaria.api.service;

import com.veterinaria.api.dtos.consultaMedicaDto.ConsultaMedicaResponseDto;
import com.veterinaria.api.dtos.vacunasDto.VacunaEditRequestDto;
import com.veterinaria.api.dtos.vacunasDto.VacunasRequestDto;
import com.veterinaria.api.dtos.vacunasDto.VacunasResponseDto;
import com.veterinaria.api.entity.*;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.MascotaRepository;
import com.veterinaria.api.repository.VacunasRepository;
import com.veterinaria.api.repository.VeterinariaRepository;
import com.veterinaria.api.repository.VeterinarioRepository;
import com.veterinaria.api.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacunasService {

    private final VacunasRepository vacunasRepository; //inyeccion de dependencias sin constructor codeeado gracias a @required

    private final MascotaRepository mascotaRepository;

    private final VeterinarioRepository veterinarioRepository;


    private final VeterinariaRepository veterinariaRepository;



    //metodo para guardar un registro de vacuna
    @Transactional // <-- Para asegurar que se guarde todo o se deshaga si hay error
    public VacunasResponseDto save(VacunasRequestDto vacunasRequestDto){
        LocalDate hoyArgentina = LocalDate.now(ZoneId.of("America/Argentina/Jujuy"));
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        Veterinaria veterinaria=veterinariaRepository.findById(veterinariaId).orElseThrow(()->new ResourceNotFoundException(
                "No existe esa veterinaria con el id "+veterinariaId
        ));

        // validar si existe el veterinario y la mascota
        // en caso de que lo encuentre lo guarda en el objeto, si no, lanza inmediatamente la excepción
        Mascota mascota=  mascotaRepository.findByIdAndEstadoTrueAndVeterinariaId(vacunasRequestDto.mascotaId(),veterinariaId)
                .orElseThrow(()->
                new ResourceNotFoundException("No se encontró a la mascota con id "+vacunasRequestDto.mascotaId()));

        Veterinario veterinario = veterinarioRepository.findByIdAndEstadoTrueAndVeterinariaId
                (vacunasRequestDto.veterinarioId(),veterinariaId).orElseThrow(()->
                new ResourceNotFoundException("No se encontró al veterinario con id  "+vacunasRequestDto.veterinarioId()));

        //validar que  la fecha de aplicacion no sea posterior a esa fecha, aca no existen viajes en el futuro
        if(vacunasRequestDto.fechaAplicacion().isAfter(hoyArgentina)){
            throw new BadRequestException("La fecha de aplicación no debe ser posterior a hoy");
        }

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
                .veterinaria(veterinaria)
                .build();

        //pasar a la base de datos
        RegistroVacunas registroVacunasGuardada=vacunasRepository.save(registroVacunas);

        // pasar a dto y retornar
        return mapToDTO(registroVacunasGuardada);


    }



    //metodo para actualizar un registro de vacuna, esto es para darle al usuario la chance de editar un registro en
    //caso de que se haya mandando un moco por ejemplo con las fechas o el propio nombre de la vacuna aplicada
    @Transactional
    public VacunasResponseDto editar(Long vacunaId, VacunaEditRequestDto vacunaEditRequestDto){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa

        LocalDate hoyArgentina = LocalDate.now(ZoneId.of("America/Argentina/Jujuy"));
        //validar que las fecha de aplicacion no sea posterior al dia corriente
        if(vacunaEditRequestDto.fechaAplicacion().isAfter(hoyArgentina)){
            throw new BadRequestException("La fecha de aplicación no debe ser posterior a hoy");
        }

        //validar que la proxima dosis no sea antes de la fecha de aplicacion
        if(vacunaEditRequestDto.fechaProximaDosis().isBefore(vacunaEditRequestDto.fechaAplicacion())){
            throw  new BadRequestException("La fecha de la proxima dosis no debe ser anterior a la fecha de aplicación");
        }

        //obtenemos el registro de la vacuna a actualizar por su ID
        RegistroVacunas registroVacunas= vacunasRepository.findByIdAndVeterinariaId(vacunaId, veterinariaId).orElseThrow(()->
                new ResourceNotFoundException("No se encontró la vacuna con id "+vacunaId));



        //actualizamos los datos viejos por los entrantes que vienen desde el dto
        registroVacunas.setNombreVacuna(vacunaEditRequestDto.nombreVacuna());
        registroVacunas.setFechaAplicacion(vacunaEditRequestDto.fechaAplicacion());
        registroVacunas.setFechaProximaDosis(vacunaEditRequestDto.fechaProximaDosis());



        //guardar en la db
        RegistroVacunas vacunaActualizada=vacunasRepository.save(registroVacunas);

        return mapToDTO(vacunaActualizada);
    }

    // metodo para listar todas los registros de vacunas de un animal(por su id)

    public List<VacunasResponseDto> verHistorialVacunas(Long id){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        if(!mascotaRepository.existsByIdAndVeterinariaId(id,veterinariaId)){
            throw  new ResourceNotFoundException("No se encontró a la mascota con id "+id);
        }

        // buscar en la db la lista de todas la consultas de una mascota y luego a eso pasarlo a dto y retonar, algo mas directp
        return vacunasRepository.findByMascotaIdAndVeterinariaIdOrderByFechaAplicacionDesc(id, veterinariaId)
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
