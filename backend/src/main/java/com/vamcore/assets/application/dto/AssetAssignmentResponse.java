package com.vamcore.assets.application.dto;

import com.vamcore.assets.domain.model.AssetAssignment;

import java.time.Instant;
import java.util.UUID;

public record AssetAssignmentResponse(UUID id, UUID assetId, UUID assigneeId, Instant assignedAt) {
    public static AssetAssignmentResponse from(AssetAssignment a) {
        return new AssetAssignmentResponse(a.id(), a.assetId(), a.assigneeId(), a.assignedAt());
    }
}
