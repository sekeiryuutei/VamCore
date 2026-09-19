package com.vamcore.assets.domain.event;

import com.vamcore.shared.domain.event.AbstractDomainEvent;

import java.util.UUID;

public class AssetAssigned extends AbstractDomainEvent {

    private final UUID assetId;
    private final UUID assigneeId;

    public AssetAssigned(UUID tenantId, UUID assetId, UUID assigneeId) {
        super(tenantId);
        this.assetId = assetId;
        this.assigneeId = assigneeId;
    }

    public UUID assetId() { return assetId; }
    public UUID assigneeId() { return assigneeId; }

    @Override
    public String eventType() {
        return "AssetAssigned";
    }
}
