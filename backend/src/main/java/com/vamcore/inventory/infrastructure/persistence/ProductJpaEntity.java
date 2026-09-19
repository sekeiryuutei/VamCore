package com.vamcore.inventory.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory_product", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "sku"}))
public class ProductJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;
    private String category;

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure;

    @Column(name = "tracking_type", nullable = false)
    private String trackingType;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProductJpaEntity() {
    }

    public ProductJpaEntity(UUID id, UUID tenantId, String sku, String name, String description, String category,
                             String unitOfMeasure, String trackingType, String status, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.unitOfMeasure = unitOfMeasure;
        this.trackingType = trackingType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public String getTrackingType() { return trackingType; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
