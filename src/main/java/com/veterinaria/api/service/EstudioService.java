package com.veterinaria.api.service;

import com.veterinaria.api.dtos.estudiosComplemenetariosDto.EstudiosRequestDto;
import com.veterinaria.api.dtos.estudiosComplemenetariosDto.EstudiosResponseDto;
import com.veterinaria.api.dtos.estudiosComplemenetariosDto.ResultadoEstudiosDto;
import com.veterinaria.api.entity.ConsultaMedica;
import com.veterinaria.api.entity.Desparasitaciones;
import com.veterinaria.api.entity.EstudioComplementario;
import com.veterinaria.api.entity.enums.EstadoEstudio;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.ConsultaMedicaRepository;
import com.veterinaria.api.repository.EstudioRepository;
import com.veterinaria.api.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstudioService {

    private final EstudioRepository estudioRepository;
    private final ConsultaMedicaRepository consultaMedicaRepository;
    private final MascotaRepository mascotaRepository;
    private final CloudinaryService cloudinaryService;




    //metodo para crear una nueva orden de estudios complementarios
    @Transactional
    public EstudiosResponseDto crearOrden(EstudiosRequestDto estudiosRequestDto){
        //encontrar la consulta
        ConsultaMedica consultaMedica= consultaMedicaRepository.findById(estudiosRequestDto.consultaId()).orElseThrow(()->
                new ResourceNotFoundException("No se encontró la consulta con id "+estudiosRequestDto.consultaId()));

        //pasar de dto a entity
        EstudioComplementario estudioComplementario = EstudioComplementario.builder()
                .tipoEstudio(estudiosRequestDto.tipoEstudio())
                .descripcion(estudiosRequestDto.descripcion())
                .fechaSolicitud(LocalDate.now())
                .estado(EstadoEstudio.PENDIENTE)
                .consultaMedica(consultaMedica)
                .build();

        //guardar en la base de datos
        EstudioComplementario ordenGuardada=estudioRepository.save(estudioComplementario);

        //pasar a dto y retornar
        return mapToDTO(ordenGuardada);

    }




    // metodo para cargar el resultado cuando el usuario termine de cargar los resultados
    @Transactional
    public EstudiosResponseDto cargarResultado(Long idEstudio, ResultadoEstudiosDto resultadoEstudiosDto, MultipartFile archivo) throws IOException {
        //encontrar el estudio
        EstudioComplementario estudioComplementario= estudioRepository.findById(idEstudio).orElseThrow(()-> new
                ResourceNotFoundException(("No se encontró el estudio con id "+idEstudio)));

        //validar que el estudio esté en estado pendiente
        if (!estudioComplementario.getEstado().equals(EstadoEstudio.PENDIENTE)){
            throw  new BadRequestException("El estado del estudio debe estar en pendiente");
        }

        // Si viene archivo, lo subimos a Cloudinary
        if (archivo != null && !archivo.isEmpty()) {
            String url = cloudinaryService.subirArchivo(archivo);
            estudioComplementario.setUrlArchivo(url);
        }


        //actualizamos campos
        estudioComplementario.setFechaRealizacion(LocalDate.now());
        estudioComplementario.setEstado(EstadoEstudio.REALIZADO);
        estudioComplementario.setResultadoInforme(resultadoEstudiosDto.resultadoInforme());

        //mandar a la base de datos
        EstudioComplementario estudioCompleto=estudioRepository.save(estudioComplementario);

        //pasar de entity a dto y retornar
        return mapToDTO(estudioCompleto);



    }


    //metodo para cancelar la orden
    @Transactional
    public EstudiosResponseDto cancelarOrden(Long estudioId){
        //buscar la orden
        EstudioComplementario estudioComplementario= estudioRepository.findById(estudioId).orElseThrow(()-> new
                ResourceNotFoundException("No se econtró el estudio con id "+estudioId));

        //validar que si el estado del estudio es realizado, ya no se puede cancelar
        if (estudioComplementario.getEstado().equals(EstadoEstudio.REALIZADO)){
            throw  new BadRequestException("No se puede cancelar un estudio que ya fue realizado");
        }

        //actualizar estado
        estudioComplementario.setEstado(EstadoEstudio.CANCELADO);

        //guardar en la db
        EstudioComplementario estudioActualizado=estudioRepository.save(estudioComplementario);

        //pasar a dto y retornar
        return  mapToDTO(estudioActualizado);
    }



    //listar los estudios de una consulta
    public List<EstudiosResponseDto> verEstudiosPorConsultaId(Long consultaId){
        //validar que exista la consulta
        if(!consultaMedicaRepository.existsById(consultaId)){
            throw new ResourceNotFoundException("No existe la consulta con id"+consultaId);
        }

        return estudioRepository.findByConsultaMedicaId(consultaId).stream()
                .map(this::mapToDTO)
                .toList();
    }


    //listar todos los estudios realizados en la historia de una mascota del mas reciente
    public List<EstudiosResponseDto> verHistorialEstudios(Long mascotaId){
        //validar que exista la mascota
        if(!mascotaRepository.existsById(mascotaId)){
            throw  new ResourceNotFoundException("No existe la mascota con id "+mascotaId);
        }

        return estudioRepository.findByConsultaMedicaMascotaIdOrderByFechaRealizacionDesc(mascotaId).stream()
                .map(this::mapToDTO)
                .toList();
    }



    // Mapeador de entity a dto
    private EstudiosResponseDto mapToDTO(EstudioComplementario e) {
        return new EstudiosResponseDto(
                e.getId(),
                e.getTipoEstudio(),
                e.getDescripcion(),
                e.getFechaSolicitud(),
                e.getFechaRealizacion(),
                e.getEstado(),
                e.getResultadoInforme(),
                e.getUrlArchivo(),
                e.getConsultaMedica().getId()
        );
    }
}
