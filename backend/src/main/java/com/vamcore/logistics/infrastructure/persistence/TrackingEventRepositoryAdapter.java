package com.vamcore.logistics.infrastructure.persistence;

import com.vamcore.logistics.domain.model.TrackingEvent;
import com.vamcore.logistics.domain.repository.TrackingEventRepository;
import com.vamcore.logistics.domain.valueobject.DeliveryStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TrackingEventRepositoryAdapter implements TrackingEventRepository {

    private final SpringDataTrackingEventRepository jpaRepository;

    public TrackingEventRepositoryAdapter(SpringDataTrackingEventRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TrackingEvent save(TrackingEvent event) {
        jpaRepository.save(new TrackingEventJpaEntity(
            event.id(), event.tenantId(), event.deliveryId(), event.status().name(), event.notes(), event.occurredAt()
        ));
        return event;
    }

    @Override
    public List<TrackingEvent> findByDelivery(UUID tenantId, UUID deliveryId) {
        return jpaRepository.findAllByTenantIdAndDeliveryIdOrderByOccurredAtDesc(tenantId, deliveryId).stream()
            .map(e -> TrackingEvent.reconstitute(
                e.getId(), e.getTenantId(), e.getDeliveryId(), DeliveryStatus.valueOf(e.getStatus()), e.getNotes(), e.getOccurredAt()
            ))
            .toList();
    }
}
