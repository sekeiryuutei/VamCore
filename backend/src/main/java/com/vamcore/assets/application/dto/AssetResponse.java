package com.vamcore.assets.application.dto;

import com.vamcore.assets.domain.model.Asset;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AssetResponse(
    UUID id, String assetCode, String name, String category, String serialNumber,
    String status, UUID currentAssigneeId, Instant acquiredAt,
    BigDecimal acquisitionCost, String currency, Integer usefulLifeMonths
) {
    public static AssetResponse from(Asset asset) {
        return new AssetResponse(
            asset.id(), asset.assetCode().value(), asset.name(), asset.category(), asset.serialNumber(),
            asset.status().name(), asset.currentAssigneeId(), asset.acquiredAt(),
            asset.acquisitionCost() != null ? asset.acquisitionCost().amount() : null,
            asset.acquisitionCost() != null ? asset.acquisitionCost().currency().getCurrencyCode() : null,
            asset.usefulLifeMonths()
        );
    }
}
