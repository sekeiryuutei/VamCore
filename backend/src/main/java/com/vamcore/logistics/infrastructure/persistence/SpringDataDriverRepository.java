package com.vamcore.logistics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataDriverRepository extends JpaRepository<DriverJpaEntity, UUID> {
    List<DriverJpaEntity> findAllByTenantId(UUID tenantId);
}
