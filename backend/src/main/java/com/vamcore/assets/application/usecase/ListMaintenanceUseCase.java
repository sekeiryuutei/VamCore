package com.vamcore.assets.application.usecase;

import com.vamcore.assets.domain.model.Maintenance;
import com.vamcore.assets.domain.repository.MaintenanceRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListMaintenanceUseCase {

    private final MaintenanceRepository maintenanceRepository;

    public ListMaintenanceUseCase(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Transactional(readOnly = true)
    public List<Maintenance> handle(UUID assetId) {
        TenantId tenantId = TenantContext.get();
        return maintenanceRepository.findByAsset(tenantId.value(), assetId);
    }
}
