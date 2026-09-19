package com.vamcore.shared.infrastructure.exception;

import java.time.Instant;

/**
 * Formato de error consistente para todas las APIs de la plataforma
 * (ver sección 41 del documento de arquitectura).
 */
public record ApiError(
    Instant timestamp,
    int status,
    String code,
    String message,
    String traceId
) {
    public static ApiError of(int status, String code, String message, String traceId) {
        return new ApiError(Instant.now(), status, code, message, traceId);
    }
}
