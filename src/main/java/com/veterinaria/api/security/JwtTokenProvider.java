package com.veterinaria.api.security;

import com.veterinaria.api.entity.Usuario;
import com.veterinaria.api.repository.UsuarioRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {


    private final UsuarioRepository usuarioRepository;

    private final String jwtSecret = "claveSecretaSuperSeguraParaVeterinariaApiQueDebeSerMuyLarga123456";
    private final int jwtExpirationMs = 86400000; // 24 horas

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 1. Generar el token incluyendo el veterinariaId como claim
    public String generarToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Buscamos el usuario para extraer su veterinaria asignada
        Usuario usuario = usuarioRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para generar el token"));

        if (usuario.getVeterinaria() == null) {
            throw new RuntimeException("El usuario no tiene una veterinaria asociada.");
        }

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("veterinariaId", usuario.getVeterinaria().getId()) // <--- INYECTAMOS EL TENANT ID
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 2. Extraer el username del token
    public String obtenerUsernameDelToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 3. NUEVO: Extraer el veterinariaId del token
    public Long obtenerVeterinariaIdDelToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("veterinariaId", Long.class);
    }

    // 4. Validar que el token sea legítimo
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token JWT inválido: " + e.getMessage());
        }
        return false;
    }
}