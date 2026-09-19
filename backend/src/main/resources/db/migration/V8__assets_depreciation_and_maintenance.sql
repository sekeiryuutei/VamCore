-- ============================================================================
-- V8__assets_depreciation_and_maintenance.sql
-- Completa VamAsset: costo/vida útil para depreciación lineal (sección 34,
-- 57) y la entidad Maintenance con historial (sección 15, 57).
-- ============================================================================

ALTER TABLE asset
    ADD COLUMN acquisition_cost     NUMERIC(19,2),
    ADD COLUMN acquisition_currency VARCHAR(3),
    ADD COLUMN useful_life_months   INTEGER;

CREATE TABLE maintenance (
    id             UUID PRIMARY KEY,
    tenant_id      UUID NOT NULL REFERENCES tenant(id),
    asset_id       UUID NOT NULL REFERENCES asset(id),
    scheduled_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at   TIMESTAMPTZ,
    notes          TEXT,
    status         VARCHAR(16) NOT NULL DEFAULT 'SCHEDULED'
);

CREATE INDEX idx_maintenance_tenant_asset ON maintenance(tenant_id, asset_id);
