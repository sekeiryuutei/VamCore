package com.vamcore.logistics.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Vehículo de la flota (ver sección 18 del documento de arquitectura). */
public class Vehicle {

    public enum Status { AVAILABLE, IN_ROUTE, MAINTENANCE }

    private final UUID id;
    private final UUID tenantId;
    private String plate;
    private String model;
    private Status status;

    private Vehicle(UUID id, UUID tenantId, String plate, String model, Status status) {
        this.id = id;
        this.tenantId = tenantId;
        this.plate = plate;
        this.model = model;
        this.status = status;
    }

    public static Vehicle create(UUID tenantId, String plate, String model) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(plate, "plate es obligatorio");
        return new Vehicle(UUID.randomUUID(), tenantId, plate, model, Status.AVAILABLE);
    }

    public static Vehicle reconstitute(UUID id, UUID tenantId, String plate, String model, Status status) {
        return new Vehicle(id, tenantId, plate, model, status);
    }

    public void markInRoute() { this.status = Status.IN_ROUTE; }
    public void markAvailable() { this.status = Status.AVAILABLE; }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public String plate() { return plate; }
    public String model() { return model; }
    public Status status() { return status; }
}
