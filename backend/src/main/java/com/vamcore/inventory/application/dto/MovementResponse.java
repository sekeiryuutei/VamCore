package com.vamcore.inventory.application.dto;

import com.vamcore.inventory.domain.model.InventoryMovement;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MovementResponse(
    UUID id, UUID productId, UUID warehouseId, String movementType, BigDecimal quantity, String unit,
    String referenceType, String referenceId, UUID createdBy, Instant createdAt
) {
    public static MovementResponse from(InventoryMovement m) {
        return new MovementResponse(
            m.id(), m.productId(), m.warehouseId(), m.movementType().name(),
            m.quantity().value(), m.quantity().unit(), m.referenceType(), m.referenceId(), m.createdBy(), m.createdAt()
        );
    }
}
