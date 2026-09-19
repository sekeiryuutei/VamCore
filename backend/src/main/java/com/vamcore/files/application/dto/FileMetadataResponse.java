package com.vamcore.files.application.dto;

import com.vamcore.files.domain.model.FileMetadata;

import java.time.Instant;
import java.util.UUID;

public record FileMetadataResponse(
    UUID id, String originalFilename, String contentType, long sizeBytes, UUID uploadedBy, Instant createdAt
) {
    public static FileMetadataResponse from(FileMetadata m) {
        return new FileMetadataResponse(m.id(), m.originalFilename(), m.contentType(), m.sizeBytes(), m.uploadedBy(), m.createdAt());
    }
}
