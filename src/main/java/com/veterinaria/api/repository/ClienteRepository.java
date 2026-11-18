package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    boolean existsByTelefonoAndVeterinariaId(String telefono, Long veterinariaId); //recibe un telefono y devuelve un cliente que tenga ese telefono


    List<Cliente> findByEstadoTrueAndVeterinariaId(Long veterinariaId);
    List<Cliente> findByVeterinariaId(Long veterinariaId);

    Optional<Cliente> findByIdAndEstadoTrueAndVeterinariaId(Long id, Long veterinariaId);


    Optional<Cliente >findByIdAndVeterinariaId(Long id, Long veterinariaId);

    boolean existsByIdAndVeterinariaId(Long id, Long veterinariaId);


}
