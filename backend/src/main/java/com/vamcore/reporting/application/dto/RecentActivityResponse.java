package com.vamcore.reporting.application.dto;

import com.vamcore.audit.domain.model.AuditLog;

import java.time.Instant;

public record RecentActivityResponse(String action, String entityType, String entityId, Instant occurredAt) {
    public static RecentActivityResponse from(AuditLog log) {
        return new RecentActivityResponse(log.action(), log.entityType(), log.entityId(), log.occurredAt());
    }
}
