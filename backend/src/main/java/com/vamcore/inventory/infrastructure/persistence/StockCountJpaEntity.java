package com.vamcore.inventory.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_count")
public class StockCountJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(name = "system_quantity_at_count", nullable = false, precision = 19, scale = 4)
    private BigDecimal systemQuantityAtCount;

    @Column(name = "counted_quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal countedQuantity;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected StockCountJpaEntity() {
    }

    public StockCountJpaEntity(UUID id, UUID tenantId, UUID productId, UUID warehouseId, BigDecimal systemQuantityAtCount,
                                BigDecimal countedQuantity, String unit, String status, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.systemQuantityAtCount = systemQuantityAtCount;
        this.countedQuantity = countedQuantity;
        this.unit = unit;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getProductId() { return productId; }
    public UUID getWarehouseId() { return warehouseId; }
    public BigDecimal getSystemQuantityAtCount() { return systemQuantityAtCount; }
    public BigDecimal getCountedQuantity() { return countedQuantity; }
    public String getUnit() { return unit; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
