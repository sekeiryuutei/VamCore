package com.vamcore.audit.domain.repository;

import com.vamcore.audit.domain.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository {

    AuditLog save(AuditLog auditLog);

    /** Actividad más reciente del tenant, útil para un feed/dashboard (ver reporting). */
    List<AuditLog> findRecentByTenant(UUID tenantId, int limit);
}
