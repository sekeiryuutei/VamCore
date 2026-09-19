package com.vamcore.assets.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataMaintenanceRepository extends JpaRepository<MaintenanceJpaEntity, UUID> {

    Optional<MaintenanceJpaEntity> findFirstByTenantIdAndAssetIdAndStatusOrderByScheduledAtDesc(UUID tenantId, UUID assetId, String status);

    List<MaintenanceJpaEntity> findAllByTenantIdAndAssetIdOrderByScheduledAtDesc(UUID tenantId, UUID assetId);
}
