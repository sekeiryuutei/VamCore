package com.vamcore.assets.domain.repository;

import com.vamcore.assets.domain.model.AssetAssignment;

import java.util.List;
import java.util.UUID;

public interface AssetAssignmentRepository {

    AssetAssignment save(AssetAssignment assignment);

    /** Historial de asignaciones del activo, más reciente primero. */
    List<AssetAssignment> findByAsset(UUID tenantId, UUID assetId);
}
