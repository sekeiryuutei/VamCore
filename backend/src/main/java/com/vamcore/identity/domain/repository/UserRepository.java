package com.vamcore.identity.domain.repository;

import com.vamcore.identity.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto de salida (repository port) para User. */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    /** true si el tenant ya tiene al menos un usuario registrado (ver bootstrap de ADMIN). */
    boolean existsAnyByTenantId(UUID tenantId);

    List<User> findAllByTenantId(UUID tenantId);
}
