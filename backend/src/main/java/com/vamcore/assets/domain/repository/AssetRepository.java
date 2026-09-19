package com.vamcore.assets.domain.repository;

import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.valueobject.AssetCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository {

    Asset save(Asset asset);

    Optional<Asset> findById(UUID tenantId, UUID id);

    boolean existsByAssetCode(UUID tenantId, AssetCode assetCode);

    List<Asset> findAllByTenant(UUID tenantId);
}
