package com.vamcore.organization.application.usecase;

import com.vamcore.organization.application.command.CreateTenantCommand;
import com.vamcore.organization.domain.model.Tenant;
import com.vamcore.organization.domain.repository.TenantRepository;
import com.vamcore.shared.infrastructure.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTenantUseCase {

    private final TenantRepository tenantRepository;

    public CreateTenantUseCase(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Transactional
    public Tenant handle(CreateTenantCommand command) {
        if (command.taxId() != null && tenantRepository.existsByTaxId(command.taxId())) {
            throw new BusinessException(
                "TENANT_ALREADY_EXISTS",
                "Ya existe un tenant registrado con ese identificador fiscal",
                HttpStatus.CONFLICT
            );
        }
        Tenant tenant = Tenant.create(command.name(), command.taxId());
        return tenantRepository.save(tenant);
    }
}
