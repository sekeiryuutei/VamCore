package com.vamcore.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataWarehouseRepository extends JpaRepository<WarehouseJpaEntity, UUID> {

    Optional<WarehouseJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    List<WarehouseJpaEntity> findAllByTenantId(UUID tenantId);
}
