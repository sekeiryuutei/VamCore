-- ============================================================================
-- V9__logistics_catalogs.sql
-- Completa VamTrack: catálogos de Customer, Vehicle y Driver (ver sección 18
-- del documento de arquitectura), y una referencia opcional desde delivery.
-- ============================================================================

CREATE TABLE customer (
    id               UUID PRIMARY KEY,
    tenant_id        UUID NOT NULL REFERENCES tenant(id),
    name             VARCHAR(255) NOT NULL,
    phone            VARCHAR(64),
    default_address  VARCHAR(500)
);

CREATE TABLE vehicle (
    id         UUID PRIMARY KEY,
    tenant_id  UUID NOT NULL REFERENCES tenant(id),
    plate      VARCHAR(32) NOT NULL,
    model      VARCHAR(128),
    status     VARCHAR(16) NOT NULL DEFAULT 'AVAILABLE',
    CONSTRAINT uq_vehicle_tenant_plate UNIQUE (tenant_id, plate)
);

CREATE TABLE driver (
    id              UUID PRIMARY KEY,
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    full_name       VARCHAR(255) NOT NULL,
    license_number  VARCHAR(64),
    status          VARCHAR(16) NOT NULL DEFAULT 'AVAILABLE'
);

CREATE INDEX idx_customer_tenant ON customer(tenant_id);
CREATE INDEX idx_vehicle_tenant ON vehicle(tenant_id);
CREATE INDEX idx_driver_tenant ON driver(tenant_id);
