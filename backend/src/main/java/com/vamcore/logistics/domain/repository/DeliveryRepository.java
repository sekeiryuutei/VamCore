package com.vamcore.logistics.domain.repository;

import com.vamcore.logistics.domain.model.Delivery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);

    Optional<Delivery> findById(UUID tenantId, UUID id);

    List<Delivery> findAllByTenant(UUID tenantId);
}
