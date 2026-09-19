package com.vamcore.logistics.domain.model;

import java.util.Objects;
import java.util.UUID;

/** Conductor de la flota (ver sección 18 del documento de arquitectura). */
public class Driver {

    public enum Status { AVAILABLE, ON_DUTY }

    private final UUID id;
    private final UUID tenantId;
    private String fullName;
    private String licenseNumber;
    private Status status;

    private Driver(UUID id, UUID tenantId, String fullName, String licenseNumber, Status status) {
        this.id = id;
        this.tenantId = tenantId;
        this.fullName = fullName;
        this.licenseNumber = licenseNumber;
        this.status = status;
    }

    public static Driver create(UUID tenantId, String fullName, String licenseNumber) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(fullName, "fullName es obligatorio");
        return new Driver(UUID.randomUUID(), tenantId, fullName, licenseNumber, Status.AVAILABLE);
    }

    public static Driver reconstitute(UUID id, UUID tenantId, String fullName, String licenseNumber, Status status) {
        return new Driver(id, tenantId, fullName, licenseNumber, status);
    }

    public void markOnDuty() { this.status = Status.ON_DUTY; }
    public void markAvailable() { this.status = Status.AVAILABLE; }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public String fullName() { return fullName; }
    public String licenseNumber() { return licenseNumber; }
    public Status status() { return status; }
}
