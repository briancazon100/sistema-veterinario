package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByTelefono(String telefono); //recibe un telefono y devuelve un cliente que tenga ese telefono


}
