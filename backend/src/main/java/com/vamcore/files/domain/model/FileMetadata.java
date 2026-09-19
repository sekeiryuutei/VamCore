package com.vamcore.files.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Metadatos de un archivo almacenado (ver sección 38 del documento de
 * arquitectura). El contenido binario NUNCA se guarda en esta entidad ni en
 * PostgreSQL — vive detrás de {@link com.vamcore.files.domain.repository.FileStoragePort},
 * identificado por `storageKey`. El dominio no sabe si ese storage es un
 * disco local, S3 o Azure Blob.
 */
public class FileMetadata {

    private final UUID id;
    private final UUID tenantId;
    private final String originalFilename;
    private final String contentType;
    private final long sizeBytes;
    private final String storageKey;
    private final UUID uploadedBy;
    private final Instant createdAt;

    private FileMetadata(UUID id, UUID tenantId, String originalFilename, String contentType, long sizeBytes,
                          String storageKey, UUID uploadedBy, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.storageKey = storageKey;
        this.uploadedBy = uploadedBy;
        this.createdAt = createdAt;
    }

    public static FileMetadata create(UUID tenantId, String originalFilename, String contentType, long sizeBytes,
                                       String storageKey, UUID uploadedBy) {
        Objects.requireNonNull(tenantId, "tenantId es obligatorio");
        Objects.requireNonNull(originalFilename, "originalFilename es obligatorio");
        Objects.requireNonNull(storageKey, "storageKey es obligatorio");
        return new FileMetadata(UUID.randomUUID(), tenantId, originalFilename, contentType, sizeBytes,
            storageKey, uploadedBy, Instant.now());
    }

    public static FileMetadata reconstitute(UUID id, UUID tenantId, String originalFilename, String contentType,
                                             long sizeBytes, String storageKey, UUID uploadedBy, Instant createdAt) {
        return new FileMetadata(id, tenantId, originalFilename, contentType, sizeBytes, storageKey, uploadedBy, createdAt);
    }

    public UUID id() { return id; }
    public UUID tenantId() { return tenantId; }
    public String originalFilename() { return originalFilename; }
    public String contentType() { return contentType; }
    public long sizeBytes() { return sizeBytes; }
    public String storageKey() { return storageKey; }
    public UUID uploadedBy() { return uploadedBy; }
    public Instant createdAt() { return createdAt; }
}
