package com.vamcore.assets.infrastructure.persistence;

import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.repository.AssetRepository;
import com.vamcore.assets.domain.valueobject.AssetCode;
import com.vamcore.assets.domain.valueobject.AssetStatus;
import com.vamcore.shared.domain.valueobject.Money;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AssetRepositoryAdapter implements AssetRepository {

    private final SpringDataAssetRepository jpaRepository;

    public AssetRepositoryAdapter(SpringDataAssetRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Asset save(Asset asset) {
        AssetJpaEntity entity = new AssetJpaEntity(
            asset.id(), asset.tenantId(), asset.assetCode().value(), asset.name(), asset.category(),
            asset.serialNumber(), asset.status().name(), asset.currentAssigneeId(), asset.acquiredAt(),
            asset.acquisitionCost() != null ? asset.acquisitionCost().amount() : null,
            asset.acquisitionCost() != null ? asset.acquisitionCost().currency().getCurrencyCode() : null,
            asset.usefulLifeMonths()
        );
        jpaRepository.save(entity);
        return asset;
    }

    @Override
    public Optional<Asset> findById(UUID tenantId, UUID id) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public boolean existsByAssetCode(UUID tenantId, AssetCode assetCode) {
        return jpaRepository.existsByAssetCodeAndTenantId(assetCode.value(), tenantId);
    }

    @Override
    public List<Asset> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream()
            .map(this::toDomain)
            .toList();
    }

    private Asset toDomain(AssetJpaEntity entity) {
        Money acquisitionCost = entity.getAcquisitionCost() != null
            ? Money.of(entity.getAcquisitionCost(), entity.getAcquisitionCurrency())
            : null;
        return Asset.reconstitute(
            entity.getId(), entity.getTenantId(), AssetCode.of(entity.getAssetCode()), entity.getName(),
            entity.getCategory(), entity.getSerialNumber(), AssetStatus.valueOf(entity.getStatus()),
            entity.getCurrentAssigneeId(), entity.getAcquiredAt(), acquisitionCost, entity.getUsefulLifeMonths()
        );
    }
}
