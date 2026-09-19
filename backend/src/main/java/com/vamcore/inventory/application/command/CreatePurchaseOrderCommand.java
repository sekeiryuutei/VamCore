package com.vamcore.inventory.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePurchaseOrderCommand(String code, String supplierName, UUID productId, UUID warehouseId, BigDecimal quantityOrdered) {
}
