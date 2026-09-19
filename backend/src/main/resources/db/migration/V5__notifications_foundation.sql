-- ============================================================================
-- V5__notifications_foundation.sql
-- Fase 5 del roadmap: Notifications (módulo core transversal).
-- Ver sección 39 del documento de arquitectura.
-- ============================================================================

CREATE TABLE notification (
    id          UUID PRIMARY KEY,
    tenant_id   UUID NOT NULL REFERENCES tenant(id),
    channel     VARCHAR(16)  NOT NULL DEFAULT 'IN_APP',
    recipient   VARCHAR(255),
    subject     VARCHAR(255) NOT NULL,
    body        TEXT,
    status      VARCHAR(16)  NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    sent_at     TIMESTAMPTZ
);

CREATE INDEX idx_notification_tenant ON notification(tenant_id, created_at DESC);
