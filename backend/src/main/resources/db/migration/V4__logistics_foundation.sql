-- ============================================================================
-- V4__logistics_foundation.sql
-- Fase 4 del roadmap: VamTrack (bounded context Logistics).
-- Ver secciones 18-21 y 32 del documento de arquitectura.
-- ============================================================================

CREATE TABLE delivery (
    id                    UUID PRIMARY KEY,
    tenant_id             UUID NOT NULL REFERENCES tenant(id),
    delivery_code         VARCHAR(64)  NOT NULL,
    customer_name         VARCHAR(255) NOT NULL,
    destination_address   VARCHAR(500),
    status                VARCHAR(32)  NOT NULL DEFAULT 'CREATED',
    driver_id             UUID,
    vehicle_id            UUID,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_delivery_tenant_code UNIQUE (tenant_id, delivery_code)
);

CREATE INDEX idx_delivery_tenant ON delivery(tenant_id);
CREATE INDEX idx_delivery_tenant_status ON delivery(tenant_id, status);

-- Ledger append-only del recorrido de la entrega (ver sección 21 - Tracking).
CREATE TABLE tracking_event (
    id           UUID PRIMARY KEY,
    tenant_id    UUID NOT NULL REFERENCES tenant(id),
    delivery_id  UUID NOT NULL REFERENCES delivery(id),
    status       VARCHAR(32) NOT NULL,
    notes        TEXT,
    occurred_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tracking_event_tenant_delivery ON tracking_event(tenant_id, delivery_id);
