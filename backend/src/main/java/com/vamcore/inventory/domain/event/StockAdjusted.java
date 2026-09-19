package com.vamcore.inventory.domain.event;

import com.vamcore.inventory.domain.valueobject.MovementType;
import com.vamcore.shared.domain.event.AbstractDomainEvent;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Se publica cada vez que un movimiento afecta el stock de un producto
 * en una bodega (RECEIPT, ISSUE, ADJUSTMENT, TRANSFER_IN/OUT, etc).
 */
public class StockAdjusted extends AbstractDomainEvent {

    private final UUID productId;
    private final UUID warehouseId;
    private final MovementType movementType;
    private final BigDecimal quantity;
    private final BigDecimal resultingStock;

    public StockAdjusted(UUID tenantId, UUID productId, UUID warehouseId, MovementType movementType,
                          BigDecimal quantity, BigDecimal resultingStock) {
        super(tenantId);
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.resultingStock = resultingStock;
    }

    public UUID productId() { return productId; }
    public UUID warehouseId() { return warehouseId; }
    public MovementType movementType() { return movementType; }
    public BigDecimal quantity() { return quantity; }
    public BigDecimal resultingStock() { return resultingStock; }

    @Override
    public String eventType() {
        return "StockAdjusted";
    }
}
