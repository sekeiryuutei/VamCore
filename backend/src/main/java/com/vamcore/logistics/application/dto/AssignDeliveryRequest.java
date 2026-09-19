package com.vamcore.logistics.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignDeliveryRequest(@NotNull UUID driverId, UUID vehicleId) {
}
