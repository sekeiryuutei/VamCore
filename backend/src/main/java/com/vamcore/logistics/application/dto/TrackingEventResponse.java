package com.vamcore.logistics.application.dto;

import com.vamcore.logistics.domain.model.TrackingEvent;

import java.time.Instant;
import java.util.UUID;

public record TrackingEventResponse(UUID id, UUID deliveryId, String status, String notes, Instant occurredAt) {
    public static TrackingEventResponse from(TrackingEvent e) {
        return new TrackingEventResponse(e.id(), e.deliveryId(), e.status().name(), e.notes(), e.occurredAt());
    }
}
