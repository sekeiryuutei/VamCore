package com.vamcore.reporting.application.dto;

import com.vamcore.reporting.domain.model.PlatformSummary;

import java.util.Map;

public record PlatformSummaryResponse(
    long productCount, long warehouseCount, long assetCount, Map<String, Long> assetsByStatus,
    long deliveryCount, Map<String, Long> deliveriesByStatus
) {
    public static PlatformSummaryResponse from(PlatformSummary s) {
        return new PlatformSummaryResponse(
            s.productCount(), s.warehouseCount(), s.assetCount(), s.assetsByStatus(),
            s.deliveryCount(), s.deliveriesByStatus()
        );
    }
}
