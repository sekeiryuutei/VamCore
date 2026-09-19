package com.vamcore.logistics.application.dto;

import com.vamcore.logistics.domain.model.Driver;

import java.util.UUID;

public record DriverResponse(UUID id, String fullName, String licenseNumber, String status) {
    public static DriverResponse from(Driver d) {
        return new DriverResponse(d.id(), d.fullName(), d.licenseNumber(), d.status().name());
    }
}
