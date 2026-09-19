package com.vamcore.inventory.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferInventoryRequest(
    @NotNull UUID productId,
    @NotNull UUID sourceWarehouseId,
    @NotNull UUID targetWarehouseId,
    @NotNull @DecimalMin(value = "0.0001", message = "La cantidad debe ser positiva") BigDecimal quantity
) {
}
