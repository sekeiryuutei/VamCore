package com.vamcore.logistics.application.dto;

import com.vamcore.logistics.domain.model.Vehicle;

import java.util.UUID;

public record VehicleResponse(UUID id, String plate, String model, String status) {
    public static VehicleResponse from(Vehicle v) {
        return new VehicleResponse(v.id(), v.plate(), v.model(), v.status().name());
    }
}
