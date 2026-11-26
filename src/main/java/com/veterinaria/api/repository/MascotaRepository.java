package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Mascota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long>, JpaSpecificationExecutor<Mascota> {
    boolean existsByIdAndVeterinariaId(Long id, Long veterinariaId);
    //metodo para buscar las mascotas de un dueño(cliente)
    List<Mascota> findByClienteIdAndVeterinariaId(Long clienteId, Long veterinariaId);

    // Busca cualquier mascota cuyo nombre contenga el texto (estilo LIKE %nombre%) e ignora mayúsculas/minúsculas
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);

    //listar todas las mascotas activas
    List<Mascota> findByEstadoTrueAndVeterinariaId(Long veterinariaId);

    //buscar mascota por id y que este activo
    Optional<Mascota> findByIdAndEstadoTrueAndVeterinariaId(Long id, Long veterinariaId);

    List<Mascota> findByVeterinariaId(Long veterinariaId);

    //metodo para buscar todas las macotas de una veterinaria por paginacion
    Page<Mascota> findByEstadoTrueAndVeterinariaId(Long veterinariaId, Pageable pageable);
}
