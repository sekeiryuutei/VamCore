package com.vamcore.inventory.application.dto;

import com.vamcore.inventory.domain.model.PurchaseOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PurchaseOrderResponse(
    UUID id, String code, String supplierName, UUID productId, UUID warehouseId,
    BigDecimal quantityOrdered, String status, Instant createdAt
) {
    public static PurchaseOrderResponse from(PurchaseOrder po) {
        return new PurchaseOrderResponse(
            po.id(), po.code(), po.supplierName(), po.productId(), po.warehouseId(),
            po.quantityOrdered(), po.status().name(), po.createdAt()
        );
    }
}
