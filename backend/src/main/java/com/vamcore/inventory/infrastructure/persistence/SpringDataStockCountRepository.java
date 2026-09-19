package com.vamcore.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataStockCountRepository extends JpaRepository<StockCountJpaEntity, UUID> {

    Optional<StockCountJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    List<StockCountJpaEntity> findAllByTenantId(UUID tenantId);
}
