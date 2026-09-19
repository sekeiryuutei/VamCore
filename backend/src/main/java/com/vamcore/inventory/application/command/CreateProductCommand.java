package com.vamcore.inventory.application.command;

public record CreateProductCommand(
    String sku, String name, String description, String category,
    String unitOfMeasure, String trackingType
) {
}
