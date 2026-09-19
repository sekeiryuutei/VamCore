package com.vamcore.assets.application.usecase;

import com.vamcore.assets.application.command.CreateAssetCommand;
import com.vamcore.assets.domain.event.AssetAcquired;
import com.vamcore.assets.domain.model.Asset;
import com.vamcore.assets.domain.repository.AssetRepository;
import com.vamcore.assets.domain.valueobject.AssetCode;
import com.vamcore.shared.domain.event.EventPublisher;
import com.vamcore.shared.domain.valueobject.Money;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registra la adquisición de un nuevo activo (ver sección 54 - flujo
 * empresarial de activos: Compra -> Recepción -> Activo creado).
 */
@Service
public class CreateAssetUseCase {

    private final AssetRepository assetRepository;
    private final EventPublisher eventPublisher;

    public CreateAssetUseCase(AssetRepository assetRepository, EventPublisher eventPublisher) {
        this.assetRepository = assetRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Asset handle(CreateAssetCommand command) {
        TenantId tenantId = TenantContext.get();
        AssetCode assetCode = AssetCode.of(command.assetCode());

        if (assetRepository.existsByAssetCode(tenantId.value(), assetCode)) {
            throw new BusinessException(
                "ASSET_CODE_ALREADY_EXISTS",
                "Ya existe un activo con el código '" + assetCode + "' en este tenant",
                HttpStatus.CONFLICT
            );
        }

        Money acquisitionCost = command.acquisitionCost() != null
            ? Money.of(command.acquisitionCost(), command.currency() != null ? command.currency() : "COP")
            : null;

        Asset asset = Asset.acquire(tenantId.value(), assetCode, command.name(), command.category(),
            command.serialNumber(), acquisitionCost, command.usefulLifeMonths());
        Asset saved = assetRepository.save(asset);

        eventPublisher.publish(new AssetAcquired(tenantId.value(), saved.id(), saved.assetCode().value()));

        return saved;
    }
}
