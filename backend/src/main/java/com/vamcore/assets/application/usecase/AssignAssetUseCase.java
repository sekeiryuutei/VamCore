package com.vamcore.assets.application.usecase;

import com.vamcore.assets.application.command.AssignAssetCommand;
import com.vamcore.assets.domain.event.AssetAssigned;
import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.model.AssetAssignment;
import com.vamcore.assets.domain.repository.AssetAssignmentRepository;
import com.vamcore.assets.domain.repository.AssetRepository;
import com.vamcore.shared.domain.event.EventPublisher;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Asigna un activo a una persona (ver sección 54 - flujo empresarial:
 * Almacenamiento -> Asignación -> Uso). Registra la asignación en el
 * historial append-only {@link AssetAssignment} para trazabilidad
 * (sección 63: "¿Quién tuvo este activo? ¿Cuándo se transfirió?").
 */
@Service
public class AssignAssetUseCase {

    private final AssetRepository assetRepository;
    private final AssetAssignmentRepository assignmentRepository;
    private final EventPublisher eventPublisher;

    public AssignAssetUseCase(AssetRepository assetRepository, AssetAssignmentRepository assignmentRepository,
                               EventPublisher eventPublisher) {
        this.assetRepository = assetRepository;
        this.assignmentRepository = assignmentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Asset handle(AssignAssetCommand command) {
        TenantId tenantId = TenantContext.get();

        Asset asset = assetRepository.findById(tenantId.value(), command.assetId())
            .orElseThrow(() -> new BusinessException("ASSET_NOT_FOUND", "Activo no encontrado", HttpStatus.NOT_FOUND));

        asset.assignTo(command.assigneeId());
        Asset saved = assetRepository.save(asset);

        assignmentRepository.save(AssetAssignment.record(tenantId.value(), saved.id(), command.assigneeId()));

        eventPublisher.publish(new AssetAssigned(tenantId.value(), saved.id(), command.assigneeId()));

        return saved;
    }
}
