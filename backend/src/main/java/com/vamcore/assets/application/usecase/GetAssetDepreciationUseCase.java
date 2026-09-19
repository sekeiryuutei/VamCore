package com.vamcore.assets.application.usecase;

import com.vamcore.assets.application.dto.DepreciationResponse;
import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.repository.AssetRepository;
import com.vamcore.shared.domain.valueobject.Money;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/** GetDepreciationReport para un activo puntual (ver sección 25 - Queries). */
@Service
public class GetAssetDepreciationUseCase {

    private final AssetRepository assetRepository;

    public GetAssetDepreciationUseCase(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    public DepreciationResponse handle(UUID assetId) {
        TenantId tenantId = TenantContext.get();
        Asset asset = assetRepository.findById(tenantId.value(), assetId)
            .orElseThrow(() -> new BusinessException("ASSET_NOT_FOUND", "Activo no encontrado", HttpStatus.NOT_FOUND));

        Instant now = Instant.now();
        Money bookValue = asset.currentBookValue(now);

        return new DepreciationResponse(
            asset.id(),
            asset.acquisitionCost() != null ? asset.acquisitionCost().amount() : null,
            asset.acquisitionCost() != null ? asset.acquisitionCost().currency().getCurrencyCode() : null,
            asset.usefulLifeMonths(),
            bookValue != null ? bookValue.amount() : null,
            now
        );
    }
}
