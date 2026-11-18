package com.veterinaria.api.repository.specification;

import com.veterinaria.api.entity.Cliente;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ClienteSpecification {
    public static Specification<Cliente> filtrarClientes(
            String nombre,
            String apellido,
            String telefono,
            String direccion,
            Long veterinariaId) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Solo clientes activos
            predicates.add(
                    criteriaBuilder.isTrue(root.get("estado"))
            );

            // Obligatorio: pertenecen a la veterinaria actual
            predicates.add(
                    criteriaBuilder.equal(
                            root.get("veterinaria").get("id"),
                            veterinariaId
                    )
            );

            if (nombre != null && !nombre.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("nombre")),
                                "%" + nombre.toLowerCase() + "%"
                        )
                );
            }

            if (apellido != null && !apellido.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("apellido")),
                                "%" + apellido.toLowerCase() + "%"
                        )
                );
            }

            if (telefono != null && !telefono.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                root.get("telefono"),
                                "%" + telefono + "%"
                        )
                );
            }

            if (direccion != null && !direccion.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("direccion")),
                                "%" + direccion.toLowerCase() + "%"
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}