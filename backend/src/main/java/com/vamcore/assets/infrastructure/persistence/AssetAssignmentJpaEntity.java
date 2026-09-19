package com.vamcore.assets.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asset_assignment")
public class AssetAssignmentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "assignee_id", nullable = false)
    private UUID assigneeId;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    protected AssetAssignmentJpaEntity() {
    }

    public AssetAssignmentJpaEntity(UUID id, UUID tenantId, UUID assetId, UUID assigneeId, Instant assignedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.assigneeId = assigneeId;
        this.assignedAt = assignedAt;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getAssetId() { return assetId; }
    public UUID getAssigneeId() { return assigneeId; }
    public Instant getAssignedAt() { return assignedAt; }
}
