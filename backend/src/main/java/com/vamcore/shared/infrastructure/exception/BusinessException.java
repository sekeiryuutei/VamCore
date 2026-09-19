package com.vamcore.shared.infrastructure.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción base para errores de negocio con código identificable.
 *
 * Ejemplos de códigos usados en la plataforma (ver sección 41 del doc de
 * arquitectura): PRODUCT_NOT_FOUND, INSUFFICIENT_STOCK,
 * INVALID_TRANSFER_STATE, ASSET_ALREADY_ASSIGNED,
 * INVALID_DELIVERY_TRANSITION, TENANT_ACCESS_DENIED.
 */
public class BusinessException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public BusinessException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }
}
