package com.vamcore.logistics.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tracking_event")
public class TrackingEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Column(nullable = false)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected TrackingEventJpaEntity() {
    }

    public TrackingEventJpaEntity(UUID id, UUID tenantId, UUID deliveryId, String status, String notes, Instant occurredAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.deliveryId = deliveryId;
        this.status = status;
        this.notes = notes;
        this.occurredAt = occurredAt;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getDeliveryId() { return deliveryId; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public Instant getOccurredAt() { return occurredAt; }
}
