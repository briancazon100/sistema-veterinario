package com.veterinaria.api.service;

import com.veterinaria.api.dtos.consultaMedicaDto.ConsultaMedicaRequestDto;
import com.veterinaria.api.dtos.consultaMedicaDto.ConsultaMedicaResponseDto;
import com.veterinaria.api.entity.ConsultaMedica;
import com.veterinaria.api.entity.Mascota;
import com.veterinaria.api.entity.Veterinaria;
import com.veterinaria.api.entity.Veterinario;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.ConsultaMedicaRepository;
import com.veterinaria.api.repository.MascotaRepository;
import com.veterinaria.api.repository.VeterinariaRepository;
import com.veterinaria.api.repository.VeterinarioRepository;
import com.veterinaria.api.security.TenantContext;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultaMedicaService {

    private final ConsultaMedicaRepository consultaMedicaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final VeterinariaRepository veterinariaRepository;


    //metodo para guardar una consulta medica
    @Transactional
    public ConsultaMedicaResponseDto save (ConsultaMedicaRequestDto consultaMedicaRequestDto){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        Veterinaria veterinaria= veterinariaRepository.findById(veterinariaId).orElseThrow(()->
                new ResourceNotFoundException("No se encontró la veterinaria con id "+veterinariaId));


        //verificar si la mascota existe
        // en caso de que no exista lanzará la exception inmediatamente, de lo contrario guardará en el objeto mascota
        Mascota mascota= mascotaRepository.findByIdAndEstadoTrueAndVeterinariaId(consultaMedicaRequestDto.mascotaId(),veterinariaId).orElseThrow(
                ()-> new ResourceNotFoundException("No se encontró a la mascota con id "+consultaMedicaRequestDto.mascotaId())
        );

        //verficar si el veterinario existe
        Veterinario veterinario= veterinarioRepository.findByIdAndEstadoTrueAndVeterinariaId(consultaMedicaRequestDto.veterinarioId(), veterinariaId).orElseThrow(
                ()-> new ResourceNotFoundException("No se encontró al veterinario con id "+consultaMedicaRequestDto.veterinarioId())

        );

        //  pasar de dto a entidad para luego poder mandarle a la base de datos, y asignar fecha actual
        ConsultaMedica consulta = ConsultaMedica.builder()
                .fecha(LocalDate.now())
                .motivo(consultaMedicaRequestDto.motivo())
                .peso(consultaMedicaRequestDto.peso())
                .temperatura(consultaMedicaRequestDto.temperatura())
                .frecuencia_cardiaca(consultaMedicaRequestDto.frecuenciaCardiaca())
                .diagnostico(consultaMedicaRequestDto.diagnostico())
                .observaciones(consultaMedicaRequestDto.observaciones())
                .mascota(mascota)
                .veterinario(veterinario)
                .veterinaria(veterinaria)
                .build();

        //guardar la entidad creada en la db
        ConsultaMedica consultaMedicaGuardada= consultaMedicaRepository.save(consulta);

        // retornar el dto
        return mapToDTO(consultaMedicaGuardada);

    }



    //metodo para buscar una consulta por id
    @Transactional(readOnly = true)
    public ConsultaMedicaResponseDto findById (Long id){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //verificar si existe la consulta, si no existe lanza la exception inmediatamente, si no la guarda en el object
        ConsultaMedica consultaMedica= consultaMedicaRepository.findByIdAndVeterinariaId(id,veterinariaId ).orElseThrow(()->
                new ResourceNotFoundException(
                "No se encontró la consulta con id "+id
        ));


        //pasar a dto para poder retornar
        return mapToDTO(consultaMedica);
    }



    //metodo para listar todas las consultas por mascota_id
    @Transactional(readOnly = true)
    public List<ConsultaMedicaResponseDto> findByMascotaId(Long mascotaId){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //verificamos que exista la mascota
        if(!mascotaRepository.existsByIdAndVeterinariaId(mascotaId, veterinariaId)){
            throw  new ResourceNotFoundException("No se encontró la mascota con id "+mascotaId);
        }

        //buscar en la db la lista de consultas de la mascota y luego pasar a dto y retornar esa lista
        return consultaMedicaRepository.findByMascotaIdAndVeterinariaIdOrderByFechaDesc(mascotaId, veterinariaId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    //metodo para listar todas las consultas registradas durante el mes actual
    public List<ConsultaMedicaResponseDto> listarConsultasEsteMes(){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //obtenemos el mes actual
        YearMonth mesActual= YearMonth.now();

        //obtenemos el primer dia del mes
        LocalDate inicio= mesActual.atDay(1);

        //obtenemos el ultimo dia del mes
        LocalDate fin= mesActual.atEndOfMonth();

        //pasar a dto luego de haber buscado en ls db
        return consultaMedicaRepository.findByVeterinariaIdAndFechaBetween(veterinariaId, inicio, fin).stream()
                .map(this::mapToDTO)
                .toList();
    }

   public  ConsultaMedicaResponseDto mapToDTO(ConsultaMedica consulta) {
        return new ConsultaMedicaResponseDto(
                consulta.getId(),
                consulta.getFecha(),
                consulta.getMotivo(),
                consulta.getPeso(),
                consulta.getTemperatura(),
                consulta.getFrecuencia_cardiaca(),
                consulta.getDiagnostico(),
                consulta.getObservaciones(),
                consulta.getMascota().getId(),
                consulta.getMascota().getNombre(),
                consulta.getVeterinario().getId(),
                consulta.getVeterinario().getNombre()
        );
    }
}
