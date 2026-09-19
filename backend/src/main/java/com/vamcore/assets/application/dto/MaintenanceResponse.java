package com.vamcore.assets.application.dto;

import com.vamcore.assets.domain.model.Maintenance;

import java.time.Instant;
import java.util.UUID;

public record MaintenanceResponse(UUID id, UUID assetId, Instant scheduledAt, Instant completedAt, String notes, String status) {
    public static MaintenanceResponse from(Maintenance m) {
        return new MaintenanceResponse(m.id(), m.assetId(), m.scheduledAt(), m.completedAt(), m.notes(), m.status().name());
    }
}
