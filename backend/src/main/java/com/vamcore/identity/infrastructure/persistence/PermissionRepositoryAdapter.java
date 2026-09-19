package com.vamcore.identity.infrastructure.persistence;

import com.vamcore.identity.domain.repository.PermissionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

/**
 * Adaptador de {@link PermissionRepository}. Usa JdbcTemplate en vez de una
 * entidad JPA porque `role_permission` es una tabla de solo lectura desde
 * la aplicación (se administra por migración/seed, ver V7), no un aggregate
 * con reglas de negocio — una consulta simple es más honesta que modelar
 * una entidad JPA solo para leerla.
 */
@Repository
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final JdbcTemplate jdbcTemplate;

    public PermissionRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<String> findPermissionCodesByRoles(Set<String> roles) {
        Set<String> permissions = new HashSet<>();
        for (String role : roles) {
            permissions.addAll(jdbcTemplate.queryForList(
                "SELECT permission_code FROM role_permission WHERE role = ?", String.class, role
            ));
        }
        return permissions;
    }
}
