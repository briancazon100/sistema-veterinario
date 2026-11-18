package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    //metodo para buscar una veterinario por su matricula
    boolean existsByMatriculaAndVeterinariaId(String matricula, Long veterinariaId);

    //listar todos los veterinarios activos
    List<Veterinario> findByEstadoTrueAndVeterinariaId(Long veterinariaId);

    Optional<Veterinario> findByIdAndEstadoTrueAndVeterinariaId(Long id, Long veterinariaId);

    boolean existsByIdAndVeterinariaId(Long id, Long veterinariaId);
}
