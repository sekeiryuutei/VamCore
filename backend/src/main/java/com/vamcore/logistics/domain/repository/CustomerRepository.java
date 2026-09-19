package com.vamcore.logistics.domain.repository;

import com.vamcore.logistics.domain.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    List<Customer> findAllByTenant(UUID tenantId);
}
