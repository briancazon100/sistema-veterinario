package com.veterinaria.api.service;

import com.veterinaria.api.dtos.clienteDto.ClienteRequestDto;
import com.veterinaria.api.dtos.clienteDto.ClienteResponseDto;
import com.veterinaria.api.dtos.mascotaDto.MascotaResponseDto;
import com.veterinaria.api.entity.Cliente;
import com.veterinaria.api.entity.Mascota;
import com.veterinaria.api.entity.Veterinaria;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.ClienteRepository;
import com.veterinaria.api.repository.VeterinariaRepository;
import com.veterinaria.api.repository.specification.ClienteSpecification;
import com.veterinaria.api.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Inyecta el repositorio automáticamente por constructor
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private final VeterinariaRepository veterinariaRepository;

    //metodo para guardar cliente
    @Transactional
    public ClienteResponseDto save(ClienteRequestDto clienteRequestDto) {
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        Veterinaria veterinaria= veterinariaRepository.findById(veterinariaId).orElseThrow(()-> new ResourceNotFoundException(
                "No se encontró a la veterinaria con id "+veterinariaId
        ));


        //1 regla de negocio: verificar si el telefono ya existe
        if (clienteRepository.existsByTelefonoAndVeterinariaId(clienteRequestDto.telefono(), veterinariaId)) {
            // Lanzamos BadRequestException
            throw new BadRequestException("El teléfono '" + clienteRequestDto.telefono() + "' ya está registrado");
        }

        // 2. Mapear DTO a Entidad , asi le enviamos a la db
        Cliente cliente = Cliente.builder()
                .nombre(clienteRequestDto.nombre())
                .apellido(clienteRequestDto.apellido())
                .telefono(clienteRequestDto.telefono())
                .direccion(clienteRequestDto.direccion())
                .estado(true)
                .veterinaria(veterinaria)
                .build();

        //3. guardar en Base De Datos
        Cliente clienteGuardado = clienteRepository.save(cliente);

        // 4. Mapear Entidad a DTO para responder
        return mapToDTO(clienteGuardado);
    }

    //metodo para listar todos los clientes activos
    public List<ClienteResponseDto> findAllActivos() {
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        // Obtenemos todos los clientes de la BD y los mapeamos a DTO usando Streams
        return clienteRepository.findByEstadoTrueAndVeterinariaId(veterinariaId).stream()
                .map(this::mapToDTO)
                .toList();
    }


    //metodo para listar todos los clientes, activos e inactivos
    public List<ClienteResponseDto>findAll(){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        // Obtenemos todos los clientes de la BD y los mapeamos a DTO usando Streams
        return clienteRepository.findByVeterinariaId(veterinariaId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public Page<ClienteResponseDto> buscarClientes(String nombre, String apellido, String telefono, String direccion, Pageable pageable) {
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        // Creamos la especificación con los datos que llegaron
        Specification<Cliente> spec = ClienteSpecification.filtrarClientes(nombre, apellido, telefono, direccion, veterinariaId);

        // Ejecutamos la búsqueda dinámica
        Page<Cliente> clientes = clienteRepository.findAll(spec, pageable);

        // Mapeamos a DTO y devolvemos
        return clientes.map(this::mapToDTO);
    }


    //metodo para buscar un cliente en particular
    public ClienteResponseDto findById(Long id) {
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        // Buscamos por ID. Si no existe, lanzamos una excepción inmediatamente.
        //// Lanzamos ResourceNotFoundException
        Cliente cliente = clienteRepository.findByIdAndEstadoTrueAndVeterinariaId(id, veterinariaId).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        // Mapeamos a DTO y retornamos
       return mapToDTO(cliente);
    }


    //metodo para actualizar un cliente
    @Transactional
    public ClienteResponseDto update(Long id, ClienteRequestDto clienteRequestDto) {
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //1. validar que exista ese cliente
        Cliente cliente = clienteRepository.findByIdAndEstadoTrueAndVeterinariaId(id, veterinariaId).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado al cliente con id " + id));

        //2. verificar que si cambió de teléfono, que no se le permita cambiarlo por el de otro cliente
        if (!cliente.getTelefono().equals(clienteRequestDto.telefono()) && clienteRepository.existsByTelefonoAndVeterinariaId(clienteRequestDto.telefono(), veterinariaId)) {

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
        return mapToDTO(clienteActualizado);
    }


    //metodo para eliminar cliente
    @Transactional
    public void delete(Long id) {
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //verificar primeramente que exista ese cliente en la db
        Cliente cliente=clienteRepository.findByIdAndEstadoTrueAndVeterinariaId(id, veterinariaId).orElseThrow(()->new ResourceNotFoundException(
        "No se encontró al cliente con id " + id
        ));



        //borrado lógico
        cliente.setEstado(false);

        //guardamos en la db el cliente con su nuevo estado =false osea inactivo
        clienteRepository.save(cliente);

    }





    //metodo para dar de nuevo alta a un cliente que fue elminado
    @Transactional
    public ClienteResponseDto cambiarAActivo(Long id){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        ///buscamos al cliente elminado por su id
        Cliente cliente= clienteRepository.findByIdAndVeterinariaId(id, veterinariaId).orElseThrow(()-> new ResourceNotFoundException(
                "No se encontró al cliente con id "+id
        ));

        //le cambiamos el estado a ACTIVO
        cliente.setEstado(true);

        //ahora guardamos en la db
        Cliente clienteActualizado=clienteRepository.save(cliente);

        //mapear a dto y retornar
        return mapToDTO(clienteActualizado);
    }
    // Método helper para no repetir el mapeo a DTO
    private ClienteResponseDto mapToDTO(Cliente cliente) {

        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getTelefono(),
                cliente.getDireccion(),
                cliente.isEstado()

        );
    }


}
