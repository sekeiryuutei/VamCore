-- ============================================================================
-- V3__assets_foundation.sql
-- Fase 3 del roadmap: VamAsset (bounded context Fixed Assets).
-- Ver secciones 15-17 y 32 del documento de arquitectura.
-- ============================================================================

CREATE TABLE asset (
    id                   UUID PRIMARY KEY,
    tenant_id            UUID NOT NULL REFERENCES tenant(id),
    asset_code           VARCHAR(64)  NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    category             VARCHAR(128),
    serial_number        VARCHAR(128),
    status               VARCHAR(32)  NOT NULL DEFAULT 'ACQUIRED',
    current_assignee_id  UUID,
    acquired_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_asset_tenant_code UNIQUE (tenant_id, asset_code)
);

CREATE INDEX idx_asset_tenant ON asset(tenant_id);
CREATE INDEX idx_asset_tenant_status ON asset(tenant_id, status);

-- Historial append-only de asignaciones (ver sección 63 - Trazabilidad).
CREATE TABLE asset_assignment (
    id           UUID PRIMARY KEY,
    tenant_id    UUID NOT NULL REFERENCES tenant(id),
    asset_id     UUID NOT NULL REFERENCES asset(id),
    assignee_id  UUID NOT NULL,
    assigned_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_asset_assignment_tenant_asset ON asset_assignment(tenant_id, asset_id);
