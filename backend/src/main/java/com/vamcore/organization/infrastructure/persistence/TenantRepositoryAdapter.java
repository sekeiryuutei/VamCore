package com.vamcore.organization.infrastructure.persistence;

import com.vamcore.organization.domain.model.Tenant;
import com.vamcore.organization.domain.repository.TenantRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida: implementa el puerto de dominio TenantRepository
 * usando Spring Data JPA + PostgreSQL.
 */
@Repository
public class TenantRepositoryAdapter implements TenantRepository {

    private final SpringDataTenantRepository jpaRepository;

    public TenantRepositoryAdapter(SpringDataTenantRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tenant save(Tenant tenant) {
        TenantJpaEntity entity = new TenantJpaEntity(
            tenant.id(),
            tenant.name(),
            tenant.taxId(),
            tenant.status().name(),
            tenant.createdAt()
        );
        jpaRepository.save(entity);
        return tenant;
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByTaxId(String taxId) {
        return jpaRepository.existsByTaxId(taxId);
    }

    private Tenant toDomain(TenantJpaEntity entity) {
        return Tenant.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getTaxId(),
            Tenant.TenantStatus.valueOf(entity.getStatus()),
            entity.getCreatedAt()
        );
    }
}
