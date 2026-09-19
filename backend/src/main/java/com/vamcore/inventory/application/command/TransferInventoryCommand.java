package com.vamcore.inventory.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferInventoryCommand(
    UUID productId, UUID sourceWarehouseId, UUID targetWarehouseId, BigDecimal quantity
) {
}
