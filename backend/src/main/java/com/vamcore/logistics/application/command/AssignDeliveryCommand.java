package com.vamcore.logistics.application.command;

import java.util.UUID;

public record AssignDeliveryCommand(UUID deliveryId, UUID driverId, UUID vehicleId) {
}
