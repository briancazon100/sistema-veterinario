package com.veterinaria.api.security;

import com.veterinaria.api.entity.Usuario;
import com.veterinaria.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos el usuario en la BD
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Como ahora cada usuario tiene un único rol, lo convertimos en una "autoridad" para Spring
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(usuario.getRol().getNombre());

        // Retornamos el objeto User que entiende Spring Security
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singleton(authority)
        );
    }

}
