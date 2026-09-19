package com.vamcore.logistics.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDriverRequest(@NotBlank String fullName, String licenseNumber) {
}
