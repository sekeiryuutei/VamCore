package com.vamcore.inventory.application.command;

import java.math.BigDecimal;
import java.util.UUID;

/** quantityDelta puede ser positivo (sobrante) o negativo (faltante). */
public record AdjustStockCommand(
    UUID productId, UUID warehouseId, BigDecimal quantityDelta, String reason
) {
}
