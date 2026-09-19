package com.vamcore.inventory.application.dto;

import com.vamcore.inventory.domain.model.Warehouse;

import java.util.UUID;

public record WarehouseResponse(UUID id, String name, String address, UUID branchId, boolean active) {
    public static WarehouseResponse from(Warehouse warehouse) {
        return new WarehouseResponse(warehouse.id(), warehouse.name(), warehouse.address(), warehouse.branchId(), warehouse.active());
    }
}
