package com.vamcore.assets.application.usecase;

import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.repository.AssetRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAssetsUseCase {

    private final AssetRepository assetRepository;

    public ListAssetsUseCase(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Transactional(readOnly = true)
    public List<Asset> handle() {
        TenantId tenantId = TenantContext.get();
        return assetRepository.findAllByTenant(tenantId.value());
    }
}
