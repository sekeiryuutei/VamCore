package com.vamcore.logistics.infrastructure.persistence;

import com.vamcore.logistics.domain.model.Driver;
import com.vamcore.logistics.domain.repository.DriverRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class DriverRepositoryAdapter implements DriverRepository {

    private final SpringDataDriverRepository jpaRepository;

    public DriverRepositoryAdapter(SpringDataDriverRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Driver save(Driver driver) {
        jpaRepository.save(new DriverJpaEntity(driver.id(), driver.tenantId(), driver.fullName(), driver.licenseNumber(), driver.status().name()));
        return driver;
    }

    @Override
    public List<Driver> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream()
            .map(e -> Driver.reconstitute(e.getId(), e.getTenantId(), e.getFullName(), e.getLicenseNumber(), Driver.Status.valueOf(e.getStatus())))
            .toList();
    }
}
