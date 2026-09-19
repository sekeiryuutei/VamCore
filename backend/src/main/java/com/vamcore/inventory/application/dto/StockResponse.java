package com.vamcore.inventory.application.dto;

import com.vamcore.inventory.domain.model.Stock;

import java.math.BigDecimal;
import java.util.UUID;

public record StockResponse(UUID productId, UUID warehouseId, BigDecimal quantity, String unit) {
    public static StockResponse from(Stock stock) {
        return new StockResponse(stock.productId(), stock.warehouseId(), stock.quantity().value(), stock.quantity().unit());
    }
}
