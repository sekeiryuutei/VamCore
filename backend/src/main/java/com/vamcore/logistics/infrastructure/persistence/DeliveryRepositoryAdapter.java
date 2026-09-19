package com.vamcore.logistics.infrastructure.persistence;

import com.vamcore.logistics.domain.model.Delivery;
import com.vamcore.logistics.domain.repository.DeliveryRepository;
import com.vamcore.logistics.domain.valueobject.DeliveryCode;
import com.vamcore.logistics.domain.valueobject.DeliveryStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DeliveryRepositoryAdapter implements DeliveryRepository {

    private final SpringDataDeliveryRepository jpaRepository;

    public DeliveryRepositoryAdapter(SpringDataDeliveryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Delivery save(Delivery delivery) {
        DeliveryJpaEntity entity = new DeliveryJpaEntity(
            delivery.id(), delivery.tenantId(), delivery.deliveryCode().value(), delivery.customerName(),
            delivery.destinationAddress(), delivery.status().name(), delivery.driverId(), delivery.vehicleId(),
            delivery.createdAt()
        );
        jpaRepository.save(entity);
        return delivery;
    }

    @Override
    public Optional<Delivery> findById(UUID tenantId, UUID id) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).map(this::toDomain);
    }

    @Override
    public List<Delivery> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantId(tenantId).stream()
            .map(this::toDomain)
            .toList();
    }

    private Delivery toDomain(DeliveryJpaEntity entity) {
        return Delivery.reconstitute(
            entity.getId(), entity.getTenantId(), DeliveryCode.of(entity.getDeliveryCode()), entity.getCustomerName(),
            entity.getDestinationAddress(), DeliveryStatus.valueOf(entity.getStatus()), entity.getDriverId(),
            entity.getVehicleId(), entity.getCreatedAt()
        );
    }
}
