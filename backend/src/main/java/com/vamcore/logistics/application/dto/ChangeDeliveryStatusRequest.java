package com.vamcore.logistics.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeDeliveryStatusRequest(@NotBlank String newStatus, String notes) {
}
