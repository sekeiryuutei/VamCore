package com.vamcore.assets.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataAssetAssignmentRepository extends JpaRepository<AssetAssignmentJpaEntity, UUID> {

    List<AssetAssignmentJpaEntity> findAllByTenantIdAndAssetIdOrderByAssignedAtDesc(UUID tenantId, UUID assetId);
}
