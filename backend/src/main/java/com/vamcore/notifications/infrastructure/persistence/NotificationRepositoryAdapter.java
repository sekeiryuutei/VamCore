package com.vamcore.notifications.infrastructure.persistence;

import com.vamcore.notifications.domain.model.Notification;
import com.vamcore.notifications.domain.repository.NotificationRepository;
import com.vamcore.notifications.domain.valueobject.NotificationChannel;
import com.vamcore.notifications.domain.valueobject.NotificationStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final SpringDataNotificationRepository jpaRepository;

    public NotificationRepositoryAdapter(SpringDataNotificationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        jpaRepository.save(new NotificationJpaEntity(
            notification.id(), notification.tenantId(), notification.channel().name(), notification.recipient(),
            notification.subject(), notification.body(), notification.status().name(),
            notification.createdAt(), notification.sentAt()
        ));
        return notification;
    }

    @Override
    public List<Notification> findAllByTenant(UUID tenantId) {
        return jpaRepository.findAllByTenantIdOrderByCreatedAtDesc(tenantId).stream()
            .map(e -> Notification.reconstitute(
                e.getId(), e.getTenantId(), NotificationChannel.valueOf(e.getChannel()), e.getRecipient(),
                e.getSubject(), e.getBody(), NotificationStatus.valueOf(e.getStatus()), e.getCreatedAt(), e.getSentAt()
            ))
            .toList();
    }
}
