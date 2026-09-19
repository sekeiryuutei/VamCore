package com.vamcore.shared.infrastructure.tenant;

import com.vamcore.shared.domain.valueobject.TenantId;

/**
 * Contenedor del tenant actual para el hilo de ejecución de la request.
 *
 * Se establece en {@code identity.infrastructure.security.JwtAuthenticationFilter}
 * a partir del token JWT autenticado, NUNCA a partir de un parámetro de la URL
 * o del body de la request (ver ADR-005 - Multi-tenancy, sección 7 del
 * documento de arquitectura).
 */
public final class TenantContext {

    private static final ThreadLocal<TenantId> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(TenantId tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static TenantId get() {
        TenantId tenantId = CURRENT_TENANT.get();
        if (tenantId == null) {
            throw new IllegalStateException(
                "No hay tenant en contexto. ¿Se llamó a este código fuera de una request autenticada?");
        }
        return tenantId;
    }

    public static boolean isPresent() {
        return CURRENT_TENANT.get() != null;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
