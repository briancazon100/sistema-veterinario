package com.veterinaria.api.repository;

import com.veterinaria.api.entity.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    //metodo para buscar las mascotas de un dueño(cliente)
    List<Mascota> findByClienteId(Long clienteId);

    // Busca cualquier mascota cuyo nombre contenga el texto (estilo LIKE %nombre%) e ignora mayúsculas/minúsculas
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
}
