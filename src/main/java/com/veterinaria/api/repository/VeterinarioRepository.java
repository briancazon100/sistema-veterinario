package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    //metodo para buscar una veterinario por su matricula
    boolean existsByMatricula(String matricula);

    //listar todos los veterinarios activos
    List<Veterinario> findByEstadoTrue();

    Optional<Veterinario> findByIdAndEstadoTrue(Long id);
}
