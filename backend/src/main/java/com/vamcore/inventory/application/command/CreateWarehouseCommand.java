package com.vamcore.inventory.application.command;

import java.util.UUID;

public record CreateWarehouseCommand(String name, String address, UUID branchId) {
}
