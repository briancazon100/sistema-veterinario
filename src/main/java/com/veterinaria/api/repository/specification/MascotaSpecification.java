package com.veterinaria.api.repository.specification;

import com.veterinaria.api.entity.Mascota;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class MascotaSpecification {

    public static Specification<Mascota> filtrarMascotas(
            String nombre,
            String especie,
            String raza,
            Long clienteId,
            Long veterinariaId) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Solo mascotas activas
            predicates.add(criteriaBuilder.isTrue(root.get("estado")));

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
                                "%" + nombre.trim().toLowerCase() + "%"
                        )
                );
            }

            if (especie != null && !especie.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("especie")),
                                "%" + especie.trim().toLowerCase() + "%"
                        )
                );
            }

            if (raza != null && !raza.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("raza")),
                                "%" + raza.trim().toLowerCase() + "%"
                        )
                );
            }

            if (clienteId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("cliente").get("id"),
                                clienteId
                        )
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}