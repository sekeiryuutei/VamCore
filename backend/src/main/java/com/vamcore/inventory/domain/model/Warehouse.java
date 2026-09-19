package com.vamcore.inventory.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Bodega/almacén físico donde se guarda inventario (ver sección 11 del
 * documento de arquitectura). Puede asociarse opcionalmente a una Branch
 * del módulo `organization`, pero Inventory nunca accede directamente a
 * las tablas de Organization — solo guarda su id como referencia.
 */
public class Warehouse {

    private final UUID id;
    private final UUID tenantId;
    private final UUID branchId; // referencia por id al módulo organization (puede ser null)
    private String name;
    private String address;
    private boolean active;

    private Warehouse(UUID id, UUID tenantId, UUID branchId, String name, String address, boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.active = active;
    }

    public static Warehouse create(UUID tenantId, UUID branchId, String name, String address) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(name, "El nombre de la bodega es obligatorio");
        return new Warehouse(UUID.randomUUID(), tenantId, branchId, name, address, true);
    }

    public static Warehouse reconstitute(UUID id, UUID tenantId, UUID branchId, String name, String address, boolean active) {
        return new Warehouse(id, tenantId, branchId, name, address, active);
    }

    public void deactivate() {
        this.active = false;
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public UUID branchId() { return branchId; }
    public String name() { return name; }
    public String address() { return address; }
    public boolean active() { return active; }
}
