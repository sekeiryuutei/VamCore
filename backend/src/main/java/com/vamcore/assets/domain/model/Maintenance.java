package com.vamcore.assets.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Registro de mantenimiento de un activo (ver sección 15 y 57 del
 * documento de arquitectura). Se crea al programar mantenimiento
 * (Asset pasa a IN_MAINTENANCE) y se cierra al completarlo (Asset
 * vuelve a IN_USE) — ver ScheduleMaintenanceUseCase / CompleteMaintenanceUseCase.
 */
public class Maintenance {

    public enum Status { SCHEDULED, COMPLETED }

    private final UUID id;
    private final UUID tenantId;
    private final UUID assetId;
    private final Instant scheduledAt;
    private Instant completedAt;
    private final String notes;
    private Status status;

    private Maintenance(UUID id, UUID tenantId, UUID assetId, Instant scheduledAt, Instant completedAt,
                         String notes, Status status) {
        this.id = id;
        this.tenantId = tenantId;
        this.assetId = assetId;
        this.scheduledAt = scheduledAt;
        this.completedAt = completedAt;
        this.notes = notes;
        this.status = status;
    }

    public static Maintenance schedule(UUID tenantId, UUID assetId, String notes) {
        return new Maintenance(UUID.randomUUID(), tenantId, assetId, Instant.now(), null, notes, Status.SCHEDULED);
    }

    public static Maintenance reconstitute(UUID id, UUID tenantId, UUID assetId, Instant scheduledAt,
                                            Instant completedAt, String notes, Status status) {
        return new Maintenance(id, tenantId, assetId, scheduledAt, completedAt, notes, status);
    }

    public void complete() {
        this.completedAt = Instant.now();
        this.status = Status.COMPLETED;
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public UUID assetId() { return assetId; }
    public Instant scheduledAt() { return scheduledAt; }
    public Instant completedAt() { return completedAt; }
    public String notes() { return notes; }
    public Status status() { return status; }
}
