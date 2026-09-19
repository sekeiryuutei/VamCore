package com.vamcore.logistics.infrastructure.web;

import com.vamcore.logistics.application.dto.*;
import com.vamcore.logistics.application.usecase.LogisticsCatalogUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Catálogos auxiliares de VamTrack: clientes, vehículos y conductores
 * (ver sección 18 del documento de arquitectura).
 */
@RestController
@RequestMapping("/api/v1/logistics")
public class LogisticsCatalogController {

    private final LogisticsCatalogUseCase catalogUseCase;

    public LogisticsCatalogController(LogisticsCatalogUseCase catalogUseCase) {
        this.catalogUseCase = catalogUseCase;
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('DELIVERY_MANAGE')")
    public CustomerResponse createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return CustomerResponse.from(catalogUseCase.createCustomer(request.name(), request.phone(), request.defaultAddress()));
    }

    @GetMapping("/customers")
    public List<CustomerResponse> listCustomers() {
        return catalogUseCase.listCustomers().stream().map(CustomerResponse::from).toList();
    }

    @PostMapping("/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('DELIVERY_MANAGE')")
    public VehicleResponse createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        return VehicleResponse.from(catalogUseCase.createVehicle(request.plate(), request.model()));
    }

    @GetMapping("/vehicles")
    public List<VehicleResponse> listVehicles() {
        return catalogUseCase.listVehicles().stream().map(VehicleResponse::from).toList();
    }

    @PostMapping("/drivers")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('DELIVERY_MANAGE')")
    public DriverResponse createDriver(@Valid @RequestBody CreateDriverRequest request) {
        return DriverResponse.from(catalogUseCase.createDriver(request.fullName(), request.licenseNumber()));
    }

    @GetMapping("/drivers")
    public List<DriverResponse> listDrivers() {
        return catalogUseCase.listDrivers().stream().map(DriverResponse::from).toList();
    }
}
