package com.vamcore.logistics.infrastructure.persistence;

import com.vamcore.logistics.domain.model.Customer;
import com.vamcore.logistics.domain.repository.CustomerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final SpringDataCustomerRepository jpaRepository;

    public CustomerRepositoryAdapter(SpringDataCustomerRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        jpaRepository.save(new CustomerJpaEntity(customer.id(), customer.tenantId(), customer.name(), customer.phone(), customer.defaultAddress()));
        return customer;
    }

    @Override
    public List<Customer> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream()
            .map(e -> Customer.reconstitute(e.getId(), e.getTenantId(), e.getName(), e.getPhone(), e.getDefaultAddress()))
            .toList();
    }
}
