package com.vamcore.audit.infrastructure.persistence;

import com.vamcore.audit.domain.model.AuditLog;
import com.vamcore.audit.domain.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final SpringDataAuditLogRepository jpaRepository;

    public AuditLogRepositoryAdapter(SpringDataAuditLogRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        jpaRepository.save(new AuditLogJpaEntity(
            auditLog.id(), auditLog.tenantId(), auditLog.action(), auditLog.entityType(),
            auditLog.entityId(), auditLog.details(), auditLog.occurredAt()
        ));
        return auditLog;
    }

    @Override
    public List<AuditLog> findRecentByTenant(UUID tenantId, int limit) {
        return jpaRepository.findAllByTenantIdOrderByOccurredAtDesc(tenantId, PageRequest.of(0, limit)).stream()
            .map(e -> AuditLog.reconstitute(e.getId(), e.getTenantId(), e.getAction(), e.getEntityType(), e.getEntityId(), e.getDetails(), e.getOccurredAt()))
            .toList();
    }
}
