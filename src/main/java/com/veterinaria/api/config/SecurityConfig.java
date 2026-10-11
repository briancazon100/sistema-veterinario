package com.veterinaria.api.config;

import com.veterinaria.api.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactivamos CSRF porque usamos JWT (APIs REST sin estado)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No usamos sesiones HTTP
                .authorizeHttpRequests(auth -> auth
                        // 1. Endpoints públicos (para poder loguearse o registrarse)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 2. Rutas del Veterinario / Admin
                        .requestMatchers("/api/v1/veterinarios/**", "/api/v1/estudios/**", "/api/v1/consultas-medicas/**",
                                "/api/v1/vacunas/**", "/api/v1/desparasitaciones/**").hasRole("ADMIN")

                        // 3. Rutas  Accesibles por ADMIN y USER)
                        .requestMatchers("/api/v1/turnos/**", "/api/v1/clientes/**", "/api/v1/mascotas/**").hasAnyRole("ADMIN", "USER")

                        // Cualquier otra ruta del sistema exige estar autenticado
                        .anyRequest().authenticated()
                )
                // Insertamos nuestro filtro de JWT antes del filtro tradicional de autenticación
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean necesario para procesar el login en el futuro controlador de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Bean para encriptar las contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
