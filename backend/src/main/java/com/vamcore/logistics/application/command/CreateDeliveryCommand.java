package com.vamcore.logistics.application.command;

public record CreateDeliveryCommand(String deliveryCode, String customerName, String destinationAddress) {
}
