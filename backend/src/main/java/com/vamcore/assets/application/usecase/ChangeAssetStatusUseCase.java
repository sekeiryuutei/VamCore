package com.vamcore.assets.application.usecase;

import com.vamcore.assets.application.command.ChangeAssetStatusCommand;
import com.vamcore.assets.domain.event.AssetStatusChanged;
import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.repository.AssetRepository;
import com.vamcore.assets.domain.valueobject.AssetStatus;
import com.vamcore.shared.domain.event.EventPublisher;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aplica una transición de estado del ciclo de vida del activo (ver
 * sección 16). Las transiciones inválidas (ej. DISPOSED -> IN_USE) son
 * rechazadas por el propio aggregate {@link Asset#changeStatus}.
 *
 * Dar de baja definitivamente (DISPOSED) requiere el permiso ASSET_DISPOSE,
 * reservado a ADMIN (ver V7__permissions_foundation.sql) — a diferencia del
 * resto de transiciones, que solo requieren ASSET_MANAGE (ya validado en
 * el controller). Esta es una regla de NEGOCIO, no solo de endpoint: por
 * eso vive aquí y no únicamente en un @PreAuthorize.
 */
@Service
public class ChangeAssetStatusUseCase {

    private final AssetRepository assetRepository;
    private final EventPublisher eventPublisher;

    public ChangeAssetStatusUseCase(AssetRepository assetRepository, EventPublisher eventPublisher) {
        this.assetRepository = assetRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Asset handle(ChangeAssetStatusCommand command) {
        TenantId tenantId = TenantContext.get();

        Asset asset = assetRepository.findById(tenantId.value(), command.assetId())
            .orElseThrow(() -> new BusinessException("ASSET_NOT_FOUND", "Activo no encontrado", HttpStatus.NOT_FOUND));

        AssetStatus previousStatus = asset.status();
        AssetStatus newStatus = AssetStatus.valueOf(command.newStatus().toUpperCase());

        if (newStatus == AssetStatus.DISPOSED) {
            assertHasPermission("ASSET_DISPOSE");
        }

        asset.changeStatus(newStatus);
        Asset saved = assetRepository.save(asset);

        eventPublisher.publish(new AssetStatusChanged(tenantId.value(), saved.id(), previousStatus, newStatus));

        return saved;
    }

    private void assertHasPermission(String permissionCode) {
        boolean granted = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(permissionCode));
        if (!granted) {
            throw new BusinessException(
                "FORBIDDEN_INSUFFICIENT_PERMISSION",
                "No tienes el permiso '" + permissionCode + "' requerido para esta operación",
                HttpStatus.FORBIDDEN
            );
        }
    }
}
