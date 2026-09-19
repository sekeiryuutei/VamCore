package com.vamcore.inventory.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchase_order", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "code"}))
public class PurchaseOrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String code;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(name = "quantity_ordered", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantityOrdered;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PurchaseOrderJpaEntity() {
    }

    public PurchaseOrderJpaEntity(UUID id, UUID tenantId, String code, String supplierName, UUID productId,
                                   UUID warehouseId, BigDecimal quantityOrdered, String status, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.code = code;
        this.supplierName = supplierName;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantityOrdered = quantityOrdered;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getCode() { return code; }
    public String getSupplierName() { return supplierName; }
    public UUID getProductId() { return productId; }
    public UUID getWarehouseId() { return warehouseId; }
    public BigDecimal getQuantityOrdered() { return quantityOrdered; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
