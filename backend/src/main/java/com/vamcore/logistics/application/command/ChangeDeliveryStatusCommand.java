package com.vamcore.logistics.application.command;

import java.util.UUID;

public record ChangeDeliveryStatusCommand(UUID deliveryId, String newStatus, String notes) {
}
