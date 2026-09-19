package com.vamcore.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataStockRepository extends JpaRepository<StockJpaEntity, UUID> {

    Optional<StockJpaEntity> findByTenantIdAndProductIdAndWarehouseId(UUID tenantId, UUID productId, UUID warehouseId);

    List<StockJpaEntity> findAllByTenantIdAndProductId(UUID tenantId, UUID productId);
}
