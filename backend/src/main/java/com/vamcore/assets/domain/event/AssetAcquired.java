package com.vamcore.assets.domain.event;

import com.vamcore.shared.domain.event.AbstractDomainEvent;

import java.util.UUID;

public class AssetAcquired extends AbstractDomainEvent {

    private final UUID assetId;
    private final String assetCode;

    public AssetAcquired(UUID tenantId, UUID assetId, String assetCode) {
        super(tenantId);
        this.assetId = assetId;
        this.assetCode = assetCode;
    }

    public UUID assetId() { return assetId; }
    public String assetCode() { return assetCode; }

    @Override
    public String eventType() {
        return "AssetAcquired";
    }
}
