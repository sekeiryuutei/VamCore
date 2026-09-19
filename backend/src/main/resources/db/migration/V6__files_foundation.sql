-- ============================================================================
-- V6__files_foundation.sql
-- Fase 5 del roadmap: Files (módulo core transversal).
-- Ver sección 38 del documento de arquitectura. El contenido binario NO se
-- guarda aquí; esta tabla solo indexa metadatos (ver LocalFileStorageAdapter).
-- ============================================================================

CREATE TABLE file_metadata (
    id                 UUID PRIMARY KEY,
    tenant_id          UUID NOT NULL REFERENCES tenant(id),
    original_filename  VARCHAR(500) NOT NULL,
    content_type       VARCHAR(255),
    size_bytes         BIGINT NOT NULL,
    storage_key        VARCHAR(500) NOT NULL UNIQUE,
    uploaded_by        UUID,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_file_metadata_tenant ON file_metadata(tenant_id, created_at DESC);
