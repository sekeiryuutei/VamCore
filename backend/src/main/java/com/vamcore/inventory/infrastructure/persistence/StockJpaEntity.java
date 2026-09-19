package com.vamcore.inventory.infrastructure.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Fila de saldo actual por producto+bodega. La columna `version` es la
 * implementación técnica del optimistic locking exigido en la sección 42
 * del documento de arquitectura (Concurrencia): si dos requests intentan
 * modificar el mismo Stock a la vez, Hibernate lanza
 * OptimisticLockException en la segunda escritura.
 */
@Entity
@Table(name = "inventory_stock", uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "product_id", "warehouse_id"}))
public class StockJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "warehouse_id", nullable = false)
    private UUID warehouseId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(nullable = false)
    private String unit;

    @Version
    @Column(nullable = false)
    private long version;

    protected StockJpaEntity() {
    }

    public StockJpaEntity(UUID id, UUID tenantId, UUID productId, UUID warehouseId,
                           BigDecimal quantity, String unit, long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.unit = unit;
        this.version = version;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public UUID getProductId() { return productId; }
    public UUID getWarehouseId() { return warehouseId; }
    public BigDecimal getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public long getVersion() { return version; }
}
