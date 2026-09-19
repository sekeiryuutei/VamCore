package com.vamcore.inventory.application.dto;

import com.vamcore.inventory.domain.model.Product;

import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
    UUID id, String sku, String name, String description, String category,
    String unitOfMeasure, String trackingType, String status, Instant createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.id(), product.sku().value(), product.name(), product.description(), product.category(),
            product.unitOfMeasure(), product.trackingType().name(), product.status().name(), product.createdAt()
        );
    }
}
