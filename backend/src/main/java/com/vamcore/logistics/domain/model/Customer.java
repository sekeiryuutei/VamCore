package com.vamcore.logistics.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Cliente de logística (ver sección 18 del documento de arquitectura).
 * Catálogo simple: nombre, teléfono y dirección por defecto — suficiente
 * para no repetir el nombre del cliente en texto libre en cada Delivery.
 */
public class Customer {

    private final UUID id;
    private final UUID tenantId;
    private String name;
    private String phone;
    private String defaultAddress;

    private Customer(UUID id, UUID tenantId, String name, String phone, String defaultAddress) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.phone = phone;
        this.defaultAddress = defaultAddress;
    }

    public static Customer create(UUID tenantId, String name, String phone, String defaultAddress) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(name, "name es obligatorio");
        return new Customer(UUID.randomUUID(), tenantId, name, phone, defaultAddress);
    }

    public static Customer reconstitute(UUID id, UUID tenantId, String name, String phone, String defaultAddress) {
        return new Customer(id, tenantId, name, phone, defaultAddress);
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public String name() { return name; }
    public String phone() { return phone; }
    public String defaultAddress() { return defaultAddress; }
}
