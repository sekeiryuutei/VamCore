package com.vamcore.assets.application.usecase;

import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.model.Maintenance;
import com.vamcore.assets.domain.repository.AssetRepository;
import com.vamcore.assets.domain.repository.MaintenanceRepository;
import com.vamcore.assets.domain.valueobject.AssetStatus;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Cierra el mantenimiento en curso y devuelve el Asset a IN_USE. */
@Service
public class CompleteMaintenanceUseCase {

    private final AssetRepository assetRepository;
    private final MaintenanceRepository maintenanceRepository;

    public CompleteMaintenanceUseCase(AssetRepository assetRepository, MaintenanceRepository maintenanceRepository) {
        this.assetRepository = assetRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Transactional
    public Maintenance handle(UUID assetId) {
        TenantId tenantId = TenantContext.get();

        Asset asset = assetRepository.findById(tenantId.value(), assetId)
            .orElseThrow(() -> new BusinessException("ASSET_NOT_FOUND", "Activo no encontrado", HttpStatus.NOT_FOUND));
        Maintenance maintenance = maintenanceRepository.findScheduledByAsset(tenantId.value(), assetId)
            .orElseThrow(() -> new BusinessException("MAINTENANCE_NOT_FOUND", "No hay mantenimiento programado para este activo", HttpStatus.NOT_FOUND));

        asset.changeStatus(AssetStatus.IN_USE);
        assetRepository.save(asset);

        maintenance.complete();
        return maintenanceRepository.save(maintenance);
    }
}
