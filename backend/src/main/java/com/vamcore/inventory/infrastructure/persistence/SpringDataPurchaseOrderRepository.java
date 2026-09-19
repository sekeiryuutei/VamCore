package com.vamcore.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataPurchaseOrderRepository extends JpaRepository<PurchaseOrderJpaEntity, UUID> {

    Optional<PurchaseOrderJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    List<PurchaseOrderJpaEntity> findAllByTenantId(UUID tenantId);
}
