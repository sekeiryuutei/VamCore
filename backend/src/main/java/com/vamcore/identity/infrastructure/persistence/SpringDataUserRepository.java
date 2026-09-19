package com.vamcore.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmailAndTenantId(String email, UUID tenantId);

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    boolean existsByTenantId(UUID tenantId);

    List<UserJpaEntity> findAllByTenantId(UUID tenantId);
}
