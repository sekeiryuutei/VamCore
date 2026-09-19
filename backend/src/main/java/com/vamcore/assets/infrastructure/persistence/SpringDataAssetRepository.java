package com.vamcore.assets.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataAssetRepository extends JpaRepository<AssetJpaEntity, UUID> {

    Optional<AssetJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByAssetCodeAndTenantId(String assetCode, UUID tenantId);

    List<AssetJpaEntity> findAllByTenantId(UUID tenantId);
}
