-- ============================================================================
-- V2__inventory_foundation.sql
-- Fase 2 del roadmap: VamStock (bounded context Inventory).
-- Ver secciones 11-14 y 32 del documento de arquitectura.
--
-- Convención de nombres de tabla con prefijo por módulo (sección 6):
--   inventory_product, inventory_warehouse, inventory_stock, inventory_movement
-- ============================================================================

CREATE TABLE inventory_product (
    id               UUID PRIMARY KEY,
    tenant_id        UUID NOT NULL REFERENCES tenant(id),
    sku              VARCHAR(64)  NOT NULL,
    name             VARCHAR(255) NOT NULL,
    description      VARCHAR(1000),
    category         VARCHAR(128),
    unit_of_measure  VARCHAR(32)  NOT NULL,
    tracking_type    VARCHAR(16)  NOT NULL DEFAULT 'NONE',
    status           VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_product_tenant_sku UNIQUE (tenant_id, sku)
);

CREATE INDEX idx_product_tenant ON inventory_product(tenant_id);

CREATE TABLE inventory_warehouse (
    id          UUID PRIMARY KEY,
    tenant_id   UUID NOT NULL REFERENCES tenant(id),
    branch_id   UUID REFERENCES branch(id),
    name        VARCHAR(255) NOT NULL,
    address     VARCHAR(500),
    active      BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_warehouse_tenant ON inventory_warehouse(tenant_id);

-- Saldo actual por producto+bodega. `version` implementa optimistic locking
-- (ver sección 42 - Concurrencia). Es una proyección; la fuente de verdad
-- histórica es inventory_movement.
CREATE TABLE inventory_stock (
    id            UUID PRIMARY KEY,
    tenant_id     UUID NOT NULL REFERENCES tenant(id),
    product_id    UUID NOT NULL REFERENCES inventory_product(id),
    warehouse_id  UUID NOT NULL REFERENCES inventory_warehouse(id),
    quantity      NUMERIC(19,4) NOT NULL DEFAULT 0,
    unit          VARCHAR(32) NOT NULL,
    version       BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_stock_tenant_product_warehouse UNIQUE (tenant_id, product_id, warehouse_id),
    CONSTRAINT ck_stock_non_negative CHECK (quantity >= 0)
);

CREATE INDEX idx_stock_tenant_product ON inventory_stock(tenant_id, product_id);

-- Ledger de movimientos (Kardex). Es append-only: nunca se actualiza ni
-- se borra un movimiento ya creado (ver secciones 13-14).
CREATE TABLE inventory_movement (
    id              UUID PRIMARY KEY,
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    product_id      UUID NOT NULL REFERENCES inventory_product(id),
    warehouse_id    UUID NOT NULL REFERENCES inventory_warehouse(id),
    movement_type   VARCHAR(32) NOT NULL,
    quantity        NUMERIC(19,4) NOT NULL,
    unit            VARCHAR(32) NOT NULL,
    reference_type  VARCHAR(64),
    reference_id    VARCHAR(128),
    created_by      UUID,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_movement_tenant_product ON inventory_movement(tenant_id, product_id);
CREATE INDEX idx_movement_tenant_product_warehouse ON inventory_movement(tenant_id, product_id, warehouse_id);
CREATE INDEX idx_movement_created_at ON inventory_movement(created_at);
