package com.vamcore.inventory.domain.event;

import com.vamcore.shared.domain.event.AbstractDomainEvent;

import java.util.UUID;

public class ProductCreated extends AbstractDomainEvent {

    private final UUID productId;
    private final String sku;

    public ProductCreated(UUID tenantId, UUID productId, String sku) {
        super(tenantId);
        this.productId = productId;
        this.sku = sku;
    }

    public UUID productId() {
        return productId;
    }

    public String sku() {
        return sku;
    }

    @Override
    public String eventType() {
        return "ProductCreated";
    }
}
