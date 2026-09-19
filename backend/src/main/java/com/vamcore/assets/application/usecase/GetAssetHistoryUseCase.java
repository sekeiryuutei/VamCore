package com.vamcore.assets.application.usecase;

import com.vamcore.assets.domain.model.AssetAssignment;
import com.vamcore.assets.domain.repository.AssetAssignmentRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/** GetAssetHistory (ver sección 25 - Queries): historial de asignaciones de un activo. */
@Service
public class GetAssetHistoryUseCase {

    private final AssetAssignmentRepository assignmentRepository;

    public GetAssetHistoryUseCase(AssetAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional(readOnly = true)
    public List<AssetAssignment> handle(UUID assetId) {
        TenantId tenantId = TenantContext.get();
        return assignmentRepository.findByAsset(tenantId.value(), assetId);
    }
}
