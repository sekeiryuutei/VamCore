package com.vamcore.inventory.application.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateStockCountRequest(
    @NotNull UUID productId,
    @NotNull UUID warehouseId,
    @NotNull BigDecimal countedQuantity
) {
}
