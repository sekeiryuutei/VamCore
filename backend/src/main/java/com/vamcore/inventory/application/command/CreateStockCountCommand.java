package com.vamcore.inventory.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateStockCountCommand(UUID productId, UUID warehouseId, BigDecimal countedQuantity) {
}
