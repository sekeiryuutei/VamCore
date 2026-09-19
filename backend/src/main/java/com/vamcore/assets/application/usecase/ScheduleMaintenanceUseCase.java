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

/**
 * Programa mantenimiento: crea el registro {@link Maintenance} Y mueve el
 * Asset a IN_MAINTENANCE en la misma operación (antes esto era solo un
 * cambio de estado sin detalle — ver sección 13 del README, "Simplificaciones
 * conocidas", ahora resuelto).
 */
@Service
public class ScheduleMaintenanceUseCase {

    private final AssetRepository assetRepository;
    private final MaintenanceRepository maintenanceRepository;

    public ScheduleMaintenanceUseCase(AssetRepository assetRepository, MaintenanceRepository maintenanceRepository) {
        this.assetRepository = assetRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    @Transactional
    public Maintenance handle(UUID assetId, String notes) {
        TenantId tenantId = TenantContext.get();

        Asset asset = assetRepository.findById(tenantId.value(), assetId)
            .orElseThrow(() -> new BusinessException("ASSET_NOT_FOUND", "Activo no encontrado", HttpStatus.NOT_FOUND));

        asset.changeStatus(AssetStatus.IN_MAINTENANCE);
        assetRepository.save(asset);

        Maintenance maintenance = Maintenance.schedule(tenantId.value(), assetId, notes);
        return maintenanceRepository.save(maintenance);
    }
}
