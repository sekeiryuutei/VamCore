package com.vamcore.inventory.application.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateWarehouseRequest(@NotBlank String name, String address, UUID branchId) {
}
