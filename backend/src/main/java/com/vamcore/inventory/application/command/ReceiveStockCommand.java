package com.vamcore.inventory.application.command;

import java.math.BigDecimal;
import java.util.UUID;

/** Intención de registrar una recepción de mercancía (ver sección 24 - Commands). */
public record ReceiveStockCommand(
    UUID productId, UUID warehouseId, BigDecimal quantity, String referenceType, String referenceId
) {
}
