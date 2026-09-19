package com.vamcore.inventory.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ReceiveStockRequest(
    @NotNull UUID productId,
    @NotNull UUID warehouseId,
    @NotNull @DecimalMin(value = "0.0001", message = "La cantidad debe ser positiva") BigDecimal quantity,
    String referenceType,
    String referenceId
) {
}
