package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByTelefono(String telefono); //recibe un telefono y devuelve un cliente que tenga ese telefono

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    List<Cliente> findByEstadoTrue();

    Optional<Cliente> findByIdAndEstadoTrue(Long id);



}
