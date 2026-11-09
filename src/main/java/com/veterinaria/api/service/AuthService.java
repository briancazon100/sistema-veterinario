package com.veterinaria.api.service;
import com.veterinaria.api.dtos.UserDto.JwtResponseDto;
import com.veterinaria.api.dtos.UserDto.LoginRequestDto;
import com.veterinaria.api.dtos.UserDto.RegistroRequestDto;

import com.veterinaria.api.dtos.UserDto.UsuarioInfoResponseDto;
import com.veterinaria.api.dtos.clienteDto.ClienteResponseDto;
import com.veterinaria.api.entity.Cliente;
import com.veterinaria.api.entity.Rol;
import com.veterinaria.api.entity.Usuario;
import com.veterinaria.api.exception.BadRequestException;
import com.veterinaria.api.exception.ResourceNotFoundException;
import com.veterinaria.api.repository.RolRepository;
import com.veterinaria.api.repository.UsuarioRepository;
import com.veterinaria.api.security.JwtTokenProvider;
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

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.username());
        // ¡Encriptamos la contraseña antes de guardarla!
        usuario.setPassword(passwordEncoder.encode(dto.contraseña()));
        usuario.setRol(rol);

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






    // Método helper para no repetir el mapeo a DTO
    private UsuarioInfoResponseDto mapToDTO(Usuario usuario) {

        return new UsuarioInfoResponseDto(
                usuario.getUsername(),
                usuario.getRol()
        );
    }

}