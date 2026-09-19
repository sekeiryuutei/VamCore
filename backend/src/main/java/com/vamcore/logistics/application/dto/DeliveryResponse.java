package com.vamcore.logistics.application.dto;

import com.vamcore.logistics.domain.model.Delivery;

import java.time.Instant;
import java.util.UUID;

public record DeliveryResponse(
    UUID id, String deliveryCode, String customerName, String destinationAddress,
    String status, UUID driverId, UUID vehicleId, Instant createdAt
) {
    public static DeliveryResponse from(Delivery delivery) {
        return new DeliveryResponse(
            delivery.id(), delivery.deliveryCode().value(), delivery.customerName(), delivery.destinationAddress(),
            delivery.status().name(), delivery.driverId(), delivery.vehicleId(), delivery.createdAt()
        );
    }
}
