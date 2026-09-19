package com.vamcore.inventory.domain.event;

import com.vamcore.shared.domain.event.AbstractDomainEvent;

import java.math.BigDecimal;
import java.util.UUID;

public class InventoryTransferred extends AbstractDomainEvent {

    private final UUID productId;
    private final UUID sourceWarehouseId;
    private final UUID targetWarehouseId;
    private final BigDecimal quantity;

    public InventoryTransferred(UUID tenantId, UUID productId, UUID sourceWarehouseId,
                                 UUID targetWarehouseId, BigDecimal quantity) {
        super(tenantId);
        this.productId = productId;
        this.sourceWarehouseId = sourceWarehouseId;
        this.targetWarehouseId = targetWarehouseId;
        this.quantity = quantity;
    }

    public UUID productId() { return productId; }
    public UUID sourceWarehouseId() { return sourceWarehouseId; }
    public UUID targetWarehouseId() { return targetWarehouseId; }
    public BigDecimal quantity() { return quantity; }

    @Override
    public String eventType() {
        return "InventoryTransferred";
    }
}
