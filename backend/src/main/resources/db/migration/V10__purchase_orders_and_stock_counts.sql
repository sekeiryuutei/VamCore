-- ============================================================================
-- V10__purchase_orders_and_stock_counts.sql
-- Completa VamStock: Purchase Order (sección 53) y Stock Count / conteo
-- físico (sección 56). Esta migración faltaba — las entidades JPA
-- (PurchaseOrderJpaEntity, StockCountJpaEntity) ya existían en el código
-- pero sin su tabla correspondiente, causando el error de Hibernate
-- "Schema-validation: missing table [purchase_order]" al arrancar.
-- ============================================================================

CREATE TABLE purchase_order (
    id                UUID PRIMARY KEY,
    tenant_id         UUID NOT NULL REFERENCES tenant(id),
    code              VARCHAR(64)  NOT NULL,
    supplier_name     VARCHAR(255),
    product_id        UUID NOT NULL REFERENCES inventory_product(id),
    warehouse_id      UUID NOT NULL REFERENCES inventory_warehouse(id),
    quantity_ordered  NUMERIC(19,4) NOT NULL,
    status            VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_purchase_order_tenant_code UNIQUE (tenant_id, code)
);

CREATE INDEX idx_purchase_order_tenant ON purchase_order(tenant_id);

CREATE TABLE stock_count (
    id                          UUID PRIMARY KEY,
    tenant_id                   UUID NOT NULL REFERENCES tenant(id),
    product_id                  UUID NOT NULL REFERENCES inventory_product(id),
    warehouse_id                UUID NOT NULL REFERENCES inventory_warehouse(id),
    system_quantity_at_count    NUMERIC(19,4) NOT NULL,
    counted_quantity            NUMERIC(19,4) NOT NULL,
    unit                        VARCHAR(32) NOT NULL,
    status                      VARCHAR(16) NOT NULL DEFAULT 'DRAFT',
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_stock_count_tenant ON stock_count(tenant_id);
