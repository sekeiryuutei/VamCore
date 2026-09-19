package com.vamcore.assets.domain.event;

import com.vamcore.assets.domain.valueobject.AssetStatus;
import com.vamcore.shared.domain.event.AbstractDomainEvent;

import java.util.UUID;

public class AssetStatusChanged extends AbstractDomainEvent {

    private final UUID assetId;
    private final AssetStatus previousStatus;
    private final AssetStatus newStatus;

    public AssetStatusChanged(UUID tenantId, UUID assetId, AssetStatus previousStatus, AssetStatus newStatus) {
        super(tenantId);
        this.assetId = assetId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }

    public UUID assetId() { return assetId; }
    public AssetStatus previousStatus() { return previousStatus; }
    public AssetStatus newStatus() { return newStatus; }

    @Override
    public String eventType() {
        return "AssetStatusChanged";
    }
}
