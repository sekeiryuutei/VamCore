package com.vamcore.logistics.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDeliveryRequest(
    @NotBlank String deliveryCode,
    @NotBlank String customerName,
    String destinationAddress
) {
}
