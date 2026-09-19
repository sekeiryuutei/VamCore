package com.vamcore.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataInventoryMovementRepository extends JpaRepository<InventoryMovementJpaEntity, UUID> {

    List<InventoryMovementJpaEntity> findAllByTenantIdAndProductIdOrderByCreatedAtDesc(UUID tenantId, UUID productId);

    List<InventoryMovementJpaEntity> findAllByTenantIdAndProductIdAndWarehouseIdOrderByCreatedAtDesc(
        UUID tenantId, UUID productId, UUID warehouseId);
}
