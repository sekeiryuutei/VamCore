package com.vamcore.logistics.application.dto;

import com.vamcore.logistics.domain.model.Customer;

import java.util.UUID;

public record CustomerResponse(UUID id, String name, String phone, String defaultAddress) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.id(), c.name(), c.phone(), c.defaultAddress());
    }
}
