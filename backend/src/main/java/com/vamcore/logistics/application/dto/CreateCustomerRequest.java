package com.vamcore.logistics.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(@NotBlank String name, String phone, String defaultAddress) {
}
