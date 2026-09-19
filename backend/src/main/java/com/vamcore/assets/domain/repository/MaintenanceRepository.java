package com.vamcore.assets.domain.repository;

import com.vamcore.assets.domain.model.Maintenance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRepository {

    Maintenance save(Maintenance maintenance);

    Optional<Maintenance> findScheduledByAsset(UUID tenantId, UUID assetId);

    List<Maintenance> findByAsset(UUID tenantId, UUID assetId);
}
