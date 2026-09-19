package com.vamcore.assets.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro inmutable de una asignación de activo (ver sección 63 -
 * Principio de trazabilidad: "¿Quién tuvo este activo? ¿Dónde estuvo?
 * ¿Cuándo se transfirió?"). Es el equivalente, para VamAsset, del
 * InventoryMovement de VamStock.
 */
public final class AssetAssignment {

    private final UUID id;
    private final UUID tenantId;
    private final UUID assetId;
    private final UUID assigneeId;
    private final Instant assignedAt;

    private AssetAssignment(UUID id, UUID tenantId, UUID assetId, UUID assigneeId, Instant assignedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.assigneeId = assigneeId;
        this.assignedAt = assignedAt;
    }

    public static AssetAssignment record(UUID tenantId, UUID assetId, UUID assigneeId) {
        return new AssetAssignment(UUID.randomUUID(), tenantId, assetId, assigneeId, Instant.now());
    }

    public static AssetAssignment reconstitute(UUID id, UUID tenantId, UUID assetId, UUID assigneeId, Instant assignedAt) {
        return new AssetAssignment(id, tenantId, assetId, assigneeId, assignedAt);
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public UUID assetId() { return assetId; }
    public UUID assigneeId() { return assigneeId; }
    public Instant assignedAt() { return assignedAt; }
}
