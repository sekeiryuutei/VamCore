package com.vamcore.organization.infrastructure.web;

import com.vamcore.organization.application.command.CreateTenantCommand;
import com.vamcore.organization.application.dto.CreateTenantRequest;
import com.vamcore.organization.application.dto.TenantResponse;
import com.vamcore.organization.application.usecase.CreateTenantUseCase;
import com.vamcore.organization.domain.model.Tenant;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API versionada (ver sección 40 del doc de arquitectura: /api/v1/).
 *
 * El Controller es delgado: solo traduce HTTP <-> Command/Query y delega
 * toda la lógica al Use Case (ver Riesgo 4 - Lógica empresarial en controllers).
 */
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final CreateTenantUseCase createTenantUseCase;

    public TenantController(CreateTenantUseCase createTenantUseCase) {
        this.createTenantUseCase = createTenantUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponse create(@Valid @RequestBody CreateTenantRequest request) {
        Tenant tenant = createTenantUseCase.handle(new CreateTenantCommand(request.name(), request.taxId()));
        return TenantResponse.from(tenant);
    }
}
