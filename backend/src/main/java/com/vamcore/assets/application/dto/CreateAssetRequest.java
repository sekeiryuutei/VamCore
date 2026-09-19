package com.vamcore.assets.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateAssetRequest(
    @NotBlank String assetCode,
    @NotBlank String name,
    String category,
    String serialNumber,
    BigDecimal acquisitionCost,
    String currency,
    Integer usefulLifeMonths
) {
}
