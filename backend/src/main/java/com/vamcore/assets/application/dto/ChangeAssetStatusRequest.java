package com.vamcore.assets.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeAssetStatusRequest(@NotBlank String newStatus) {
}
