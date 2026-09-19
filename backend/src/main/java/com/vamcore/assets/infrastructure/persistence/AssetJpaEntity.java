package com.vamcore.assets.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "asset", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "asset_code"}))
public class AssetJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "asset_code", nullable = false)
    private String assetCode;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(nullable = false)
    private String status;

    @Column(name = "current_assignee_id")
    private UUID currentAssigneeId;

    @Column(name = "acquired_at", nullable = false, updatable = false)
    private Instant acquiredAt;

    @Column(name = "acquisition_cost", precision = 19, scale = 2)
    private BigDecimal acquisitionCost;

    @Column(name = "acquisition_currency", length = 3)
    private String acquisitionCurrency;

    @Column(name = "useful_life_months")
    private Integer usefulLifeMonths;

    protected AssetJpaEntity() {
    }

    public AssetJpaEntity(UUID id, UUID tenantId, String assetCode, String name, String category, String serialNumber,
                           String status, UUID currentAssigneeId, Instant acquiredAt, BigDecimal acquisitionCost,
                           String acquisitionCurrency, Integer usefulLifeMonths) {
        this.id = id;
        this.tenantId = tenantId;
        this.assetCode = assetCode;
        this.name = name;
        this.category = category;
        this.serialNumber = serialNumber;
        this.status = status;
        this.currentAssigneeId = currentAssigneeId;
        this.acquiredAt = acquiredAt;
        this.acquisitionCost = acquisitionCost;
        this.acquisitionCurrency = acquisitionCurrency;
        this.usefulLifeMonths = usefulLifeMonths;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getAssetCode() { return assetCode; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSerialNumber() { return serialNumber; }
    public String getStatus() { return status; }
    public UUID getCurrentAssigneeId() { return currentAssigneeId; }
    public Instant getAcquiredAt() { return acquiredAt; }
    public BigDecimal getAcquisitionCost() { return acquisitionCost; }
    public String getAcquisitionCurrency() { return acquisitionCurrency; }
    public Integer getUsefulLifeMonths() { return usefulLifeMonths; }
}
