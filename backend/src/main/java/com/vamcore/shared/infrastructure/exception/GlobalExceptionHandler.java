package com.vamcore.shared.infrastructure.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones. Garantiza que TODAS las APIs de la
 * plataforma devuelvan errores en el mismo formato (ver sección 41 del
 * documento de arquitectura).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String traceId = traceIdOf(request);
        ApiError body = ApiError.of(ex.status().value(), ex.code(), ex.getMessage(), traceId);
        return ResponseEntity.status(ex.status()).body(body);
    }

    /**
     * Los Value Objects del dominio (SKU, Quantity, TenantId, Money...) validan
     * sus invariantes lanzando IllegalArgumentException. Sin este handler, esos
     * errores de validación caían en el catch-all genérico y se veían como un
     * 500 "error inesperado" en vez de un 400 con un mensaje claro para el usuario.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String traceId = traceIdOf(request);
        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", ex.getMessage(), traceId);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        String traceId = traceIdOf(request);
        ApiError body = ApiError.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "INTERNAL_ERROR",
            "Ocurrió un error inesperado. Contacte soporte con el traceId.",
            traceId
        );
        return ResponseEntity.internalServerError().body(body);
    }

    private String traceIdOf(HttpServletRequest request) {
        Object traceId = request.getAttribute("correlationId");
        return traceId != null ? traceId.toString() : "unknown";
    }
}
