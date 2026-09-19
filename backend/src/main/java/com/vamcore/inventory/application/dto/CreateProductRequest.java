package com.vamcore.inventory.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(
    @NotBlank String sku,
    @NotBlank String name,
    String description,
    String category,
    @NotBlank String unitOfMeasure,
    String trackingType // NONE | BATCH | SERIALIZED, default NONE
) {
}
