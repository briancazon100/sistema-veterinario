package com.veterinaria.api.service;
import com.veterinaria.api.dtos.UserDto.JwtResponseDto;
import com.veterinaria.api.dtos.UserDto.LoginRequestDto;
import com.veterinaria.api.dtos.UserDto.RegistroRequestDto;

import com.veterinaria.api.dtos.UserDto.UsuarioInfoResponseDto;
import com.veterinaria.api.dtos.clienteDto.ClienteResponseDto;
import com.veterinaria.api.entity.Cliente;
import com.veterinaria.api.entity.Rol;
import com.veterinaria.api.entity.Usuario;
import com.veterinaria.api.entity.Veterinaria;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.RolRepository;
import com.veterinaria.api.repository.UsuarioRepository;
import com.veterinaria.api.repository.VeterinariaRepository;
import com.veterinaria.api.security.JwtTokenProvider;
import com.veterinaria.api.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {


    private final AuthenticationManager authenticationManager;


    private  final UsuarioRepository usuarioRepository;


    private  final RolRepository rolRepository;


    private  final PasswordEncoder passwordEncoder;


    private final JwtTokenProvider jwtTokenProvider;


    private final VeterinariaRepository veterinariaRepository;

    @Transactional
    public String registrar(RegistroRequestDto dto) {

        //validar si el username ya esta en uso
        if(usuarioRepository.existsByUsername(dto.username())){
           throw new BadRequestException("Ya existe un usuario con ese nombre de usuario");
        }

       //obtener el rol
        Rol rol = rolRepository.findByNombre(dto.rolNombre()).orElseThrow(()-> new ResourceNotFoundException(
                "Rol no encontrado"
        ));

        // 3. OBTENER EL TENANT ACTUAL (La veterinaria del admin que está haciendo la petición)
        Long veterinariaId = TenantContext.getCurrentTenant();
        Veterinaria veterinaria= veterinariaRepository.findById(veterinariaId).orElseThrow(()->new ResourceNotFoundException(
                "No se encontró a la veterinaria con id "+veterinariaId
        ));

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.username());
        // ¡Encriptamos la contraseña antes de guardarla!
        usuario.setPassword(passwordEncoder.encode(dto.contraseña()));
        usuario.setRol(rol);
        usuario.setEstado(true);
        usuario.setVeterinaria(veterinaria);

        usuarioRepository.save(usuario);
        return "Usuario registrado con éxito";
    }

    public JwtResponseDto login(LoginRequestDto dto) {

        // Autenticamos al usuario utilizando el AuthenticationManager configurado en Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.contraseña())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generamos el token JWT
        String jwt = jwtTokenProvider.generarToken(authentication);

        Usuario usuario = usuarioRepository.findByUsername(dto.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new JwtResponseDto(jwt, "Bearer", usuario.getUsername(), usuario.getRol().getNombre());
    }



    //metodo para buscar un usuario por su usernmae
    public UsuarioInfoResponseDto verInfo(String username){
        Usuario usuario= usuarioRepository.findByUsername(username).orElseThrow(()->
                new ResourceNotFoundException("No se encontró al usuario "+username));

        return mapToDTO(usuario);
    }



//metodo para listar todos los usuarios activos
public List<UsuarioInfoResponseDto> listarActivos(){
    Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
    return usuarioRepository.findByEstadoTrueAndVeterinariaId(veterinariaId).stream()
            .map(this::mapToDTO)
            .toList();
}


//metodo para desactivar un usuario
    @Transactional
    public void desactivar(Long id){
        Long veterinariaId = TenantContext.getCurrentTenant();  //obtenemos el id de la veterinaria activa
        //encontrar al usuario al cual vamos a eliminar
        Usuario usuario= usuarioRepository.findByIdAndVeterinariaId(id,veterinariaId).orElseThrow(()->new ResourceNotFoundException(
                "No se encontró al usuario con id "+id
        ));

        //cambiamos el estado a false(desactivado)
        usuario.setEstado(false);

        //guardamos los cambios en la db
        usuarioRepository.save(usuario);
    }


    // Método helper para no repetir el mapeo a DTO
    private UsuarioInfoResponseDto mapToDTO(Usuario usuario) {

        return new UsuarioInfoResponseDto(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRol(),
                usuario.isEstado()
        );
    }

}