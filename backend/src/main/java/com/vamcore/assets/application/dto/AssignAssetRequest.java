package com.vamcore.assets.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignAssetRequest(@NotNull UUID assigneeId) {
}
