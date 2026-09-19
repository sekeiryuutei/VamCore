package com.vamcore.identity.domain.repository;

import java.util.Set;

/**
 * Puerto de salida para resolver qué permisos otorga un conjunto de roles
 * (ver sección 32 del documento de arquitectura: tablas `permission` y
 * `role_permission`). Se consulta en el login para incrustar los permisos
 * como claim en el JWT (ver JwtTokenProvider) — así cada request los trae
 * consigo sin volver a golpear la base de datos.
 */
public interface PermissionRepository {

    Set<String> findPermissionCodesByRoles(Set<String> roles);
}
