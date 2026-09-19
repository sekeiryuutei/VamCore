package com.vamcore.inventory.domain.model;

import com.vamcore.inventory.domain.valueobject.SKU;
import com.vamcore.inventory.domain.valueobject.TrackingType;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root del catálogo de productos (ver secciones 11-12 del
 * documento de arquitectura). Un Product NO es lo mismo que un Asset
 * (ver sección 17 — "Relación Inventory ↔ Assets"): Product representa
 * un artículo del catálogo con cantidad, mientras que Asset representa
 * un bien individualizado con serial y asignación.
 */
public class Product {

    private final UUID id;
    private final UUID tenantId;
    private final SKU sku;
    private String name;
    private String description;
    private String category;
    private String unitOfMeasure;
    private TrackingType trackingType;
    private ProductStatus status;
    private final Instant createdAt;

    private Product(UUID id, UUID tenantId, SKU sku, String name, String description, String category,
                     String unitOfMeasure, TrackingType trackingType, ProductStatus status, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.unitOfMeasure = unitOfMeasure;
        this.trackingType = trackingType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Product create(UUID tenantId, SKU sku, String name, String description,
                                  String category, String unitOfMeasure, TrackingType trackingType) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(sku, "sku es obligatorio");
        Objects.requireNonNull(name, "name es obligatorio");
        Objects.requireNonNull(unitOfMeasure, "unitOfMeasure es obligatorio");
        TrackingType tt = trackingType != null ? trackingType : TrackingType.NONE;
        return new Product(UUID.randomUUID(), tenantId, sku, name, description, category,
            unitOfMeasure, tt, ProductStatus.ACTIVE, Instant.now());
    }

    public static Product reconstitute(UUID id, UUID tenantId, SKU sku, String name, String description,
                                        String category, String unitOfMeasure, TrackingType trackingType,
                                        ProductStatus status, Instant createdAt) {
        return new Product(id, tenantId, sku, name, description, category, unitOfMeasure, trackingType, status, createdAt);
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    public void rename(String newName) {
        Objects.requireNonNull(newName, "El nombre no puede ser null");
        this.name = newName;
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public SKU sku() { return sku; }
    public String name() { return name; }
    public String description() { return description; }
    public String category() { return category; }
    public String unitOfMeasure() { return unitOfMeasure; }
    public TrackingType trackingType() { return trackingType; }
    public ProductStatus status() { return status; }
    public Instant createdAt() { return createdAt; }

    public enum ProductStatus {
        ACTIVE, DISCONTINUED
    }
}
