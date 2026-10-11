package com.veterinaria.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Buscamos el header "Authorization" en la petición HTTP
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // El token viene con el formato: "Bearer eyJhbGciOiJIUzUxMiJ9..."
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7); // Nos quedamos solo con la parte del token
            try {
                username = tokenProvider.obtenerUsernameDelToken(token);
            } catch (Exception e) {
                logger.error("No se pudo extraer el username del token JWT: " + e.getMessage());
            }
        }

        // 2. Si encontramos un username y el usuario aún no está autenticado en este hilo
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 3. Validamos si el token es correcto
            if (tokenProvider.validarToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 4. Autenticamos al usuario formalmente en el contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Dejamos que la petición siga su curso hacia el Controller
        filterChain.doFilter(request, response);
    }
}