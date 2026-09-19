package com.vamcore.logistics.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateVehicleRequest(@NotBlank String plate, String model) {
}
