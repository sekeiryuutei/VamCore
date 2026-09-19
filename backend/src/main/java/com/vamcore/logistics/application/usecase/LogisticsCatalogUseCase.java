package com.vamcore.logistics.application.usecase;

import com.vamcore.logistics.domain.model.Customer;
import com.vamcore.logistics.domain.model.Driver;
import com.vamcore.logistics.domain.model.Vehicle;
import com.vamcore.logistics.domain.repository.CustomerRepository;
import com.vamcore.logistics.domain.repository.DriverRepository;
import com.vamcore.logistics.domain.repository.VehicleRepository;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de los catálogos auxiliares de VamTrack: Customer, Vehicle,
 * Driver (ver sección 18 del documento de arquitectura). Se agrupan en un
 * solo servicio porque cada uno es un simple create+list sin ciclo de vida
 * propio — separarlos en 6 clases no aportaría claridad adicional.
 */
@Service
public class LogisticsCatalogUseCase {

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public LogisticsCatalogUseCase(CustomerRepository customerRepository, VehicleRepository vehicleRepository,
                                    DriverRepository driverRepository) {
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public Customer createCustomer(String name, String phone, String defaultAddress) {
        return customerRepository.save(Customer.create(TenantContext.get().value(), name, phone, defaultAddress));
    }

    @Transactional(readOnly = true)
    public List<Customer> listCustomers() {
        return customerRepository.findAllByTenant(TenantContext.get().value());
    }

    @Transactional
    public Vehicle createVehicle(String plate, String model) {
        return vehicleRepository.save(Vehicle.create(TenantContext.get().value(), plate, model));
    }

    @Transactional(readOnly = true)
    public List<Vehicle> listVehicles() {
        return vehicleRepository.findAllByTenant(TenantContext.get().value());
    }

    @Transactional
    public Driver createDriver(String fullName, String licenseNumber) {
        return driverRepository.save(Driver.create(TenantContext.get().value(), fullName, licenseNumber));
    }

    @Transactional(readOnly = true)
    public List<Driver> listDrivers() {
        return driverRepository.findAllByTenant(TenantContext.get().value());
    }
}
