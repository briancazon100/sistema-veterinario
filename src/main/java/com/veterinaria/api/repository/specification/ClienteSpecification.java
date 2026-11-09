package com.veterinaria.api.repository.specification;

import com.veterinaria.api.entity.Cliente;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ClienteSpecification {

    public static Specification<Cliente> filtrarClientes(String nombre, String apellido, String telefono, String direccion) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. OBLIGATORIO: Siempre filtramos que estén activos (por el borrado lógico)
            // Reemplazá esa línea por esta:
            predicates.add(criteriaBuilder.isTrue(root.get("estado")));

            // 2. Si mandan nombre, lo agregamos a la consulta (usamos LIKE para búsqueda parcial, ej: "Mar" encuentra a "María")
            if (nombre != null && !nombre.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"
                ));
            }

            // 3. Si mandan apellido, lo agregamos
            if (apellido != null && !apellido.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("apellido")),
                        "%" + apellido.toLowerCase() + "%"
                ));
            }

            // 4. Si mandan teléfono, lo agregamos
            if (telefono != null && !telefono.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        root.get("telefono"),
                        "%" + telefono + "%"
                ));
            }

            if (direccion != null && !direccion.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        root.get("direccion"),
                        "%" + direccion + "%"
                ));
            }

            // Unimos todos los filtros condicionales con un AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}