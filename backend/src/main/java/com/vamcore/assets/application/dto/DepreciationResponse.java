package com.vamcore.assets.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DepreciationResponse(
    UUID assetId, BigDecimal acquisitionCost, String currency, Integer usefulLifeMonths,
    BigDecimal currentBookValue, Instant asOf
) {
}
