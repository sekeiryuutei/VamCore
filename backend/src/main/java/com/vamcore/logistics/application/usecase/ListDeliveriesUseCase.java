package com.vamcore.logistics.application.usecase;

import com.vamcore.logistics.domain.model.Delivery;
import com.vamcore.logistics.domain.repository.DeliveryRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListDeliveriesUseCase {

    private final DeliveryRepository deliveryRepository;

    public ListDeliveriesUseCase(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional(readOnly = true)
    public List<Delivery> handle() {
        TenantId tenantId = TenantContext.get();
        return deliveryRepository.findAllByTenant(tenantId.value());
    }
}
