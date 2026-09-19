package com.vamcore.organization.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Sede/sucursal de una organización (ver sección 10 del documento de
 * arquitectura). Ejemplo real citado en la arquitectura:
 *
 *   Empresa ABC
 *   ├── Cali
 *   │   ├── Administración
 *   │   ├── Bodega principal
 *   │   └── Operaciones
 *   └── Bogotá
 *       ├── Administración
 *       └── Bodega
 */
public class Branch {

    private final UUID id;
    private final UUID tenantId;
    private String name;
    private String address;
    private boolean active;

    private Branch(UUID id, UUID tenantId, String name, String address, boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.address = address;
        this.active = active;
    }

    public static Branch create(UUID tenantId, String name, String address) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(name, "El nombre de la sede es obligatorio");
        return new Branch(UUID.randomUUID(), tenantId, name, address, true);
    }

    public static Branch reconstitute(UUID id, UUID tenantId, String name, String address, boolean active) {
        return new Branch(id, tenantId, name, address, active);
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID id() {
        return id;
    }

    public UUID tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }

    public String address() {
        return address;
    }

    public boolean active() {
        return active;
    }
}
