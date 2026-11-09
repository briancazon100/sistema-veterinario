package com.veterinaria.api.controller;

import com.veterinaria.api.dtos.UserDto.JwtResponseDto;
import com.veterinaria.api.dtos.UserDto.LoginRequestDto;
import com.veterinaria.api.dtos.UserDto.RegistroRequestDto;
import com.veterinaria.api.dtos.UserDto.UsuarioInfoResponseDto;
import com.veterinaria.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    //endpoint para registrarse
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistroRequestDto dto){
        return ResponseEntity.ok(authService.registrar(dto));
    }

    //endpoint para loguearse
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto dto){
        return ResponseEntity.ok(authService.login(dto));
    }

    //endpoint para obtener los datos del user logueado, osea el username y el rol
    @GetMapping("/me")
    public ResponseEntity<UsuarioInfoResponseDto> obtenerMiPerfil() {
        // Obtenemos el username directamente del token validado por Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();


        return ResponseEntity.ok(authService.verInfo(username));
    }




}
