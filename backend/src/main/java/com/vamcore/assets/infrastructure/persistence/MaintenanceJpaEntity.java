package com.vamcore.assets.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "maintenance")
public class MaintenanceJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private String status;

    protected MaintenanceJpaEntity() {
    }

    public MaintenanceJpaEntity(UUID id, UUID tenantId, UUID assetId, Instant scheduledAt, Instant completedAt,
                                 String notes, String status) {
        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.scheduledAt = scheduledAt;
        this.completedAt = completedAt;
        this.notes = notes;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getAssetId() { return assetId; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }
}
