package com.vamcore.inventory.application.dto;

import com.vamcore.inventory.domain.model.StockCount;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record StockCountResponse(
    UUID id, UUID productId, UUID warehouseId, BigDecimal systemQuantityAtCount,
    BigDecimal countedQuantity, BigDecimal variance, String status, Instant createdAt
) {
    public static StockCountResponse from(StockCount sc) {
        return new StockCountResponse(
            sc.id(), sc.productId(), sc.warehouseId(), sc.systemQuantityAtCount().value(),
            sc.countedQuantity().value(), sc.variance().value(), sc.status().name(), sc.createdAt()
        );
    }
}
