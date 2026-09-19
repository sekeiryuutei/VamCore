-- ============================================================================
-- V1__core_foundation.sql
-- Fase 0 / Fase 1 del roadmap: fundación de plataforma (multi-tenant),
-- identidad básica y auditoría. Ver secciones 7, 9, 10, 32 y 37 del
-- documento de arquitectura.
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ------------------------------------------------------------------
-- ORGANIZATION
-- ------------------------------------------------------------------
CREATE TABLE tenant (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    tax_id      VARCHAR(64) UNIQUE,
    status      VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE branch (
    id          UUID PRIMARY KEY,
    tenant_id   UUID NOT NULL REFERENCES tenant(id),
    name        VARCHAR(255) NOT NULL,
    address     VARCHAR(500),
    active      BOOLEAN NOT NULL DEFAULT true,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_branch_tenant ON branch(tenant_id);

-- ------------------------------------------------------------------
-- IDENTITY
-- ------------------------------------------------------------------
CREATE TABLE app_user (
    id             UUID PRIMARY KEY,
    tenant_id      UUID NOT NULL REFERENCES tenant(id),
    email          VARCHAR(255) NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    full_name      VARCHAR(255),
    status         VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_user_tenant_email UNIQUE (tenant_id, email)
);

CREATE INDEX idx_user_tenant ON app_user(tenant_id);
CREATE INDEX idx_user_email ON app_user(email);

CREATE TABLE user_role (
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role    VARCHAR(64) NOT NULL,
    PRIMARY KEY (user_id, role)
);

-- ------------------------------------------------------------------
-- AUDIT (transversal, ver sección 37)
-- ------------------------------------------------------------------
CREATE TABLE audit_log (
    id           UUID PRIMARY KEY,
    tenant_id    UUID NOT NULL,
    action       VARCHAR(128) NOT NULL,
    entity_type  VARCHAR(128),
    entity_id    VARCHAR(128),
    details      TEXT,
    occurred_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_tenant ON audit_log(tenant_id);
CREATE INDEX idx_audit_occurred_at ON audit_log(occurred_at);
