package com.vamcore.notifications.application.usecase;

import com.vamcore.notifications.domain.model.Notification;
import com.vamcore.notifications.domain.repository.NotificationRepository;
import com.vamcore.notifications.domain.repository.NotificationSender;
import com.vamcore.notifications.domain.valueobject.NotificationChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Crea, persiste y despacha una notificación (ver sección 39: Domain Event
 * -> Notification Handler -> Notification Service -> Notification Provider).
 * Es el "Notification Service" del diagrama; lo invocan los listeners de
 * infrastructure.messaging que reaccionan a eventos de otros módulos.
 */
@Service
public class CreateNotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;

    public CreateNotificationUseCase(NotificationRepository notificationRepository, NotificationSender notificationSender) {
        this.notificationRepository = notificationRepository;
        this.notificationSender = notificationSender;
    }

    @Transactional
    public Notification handle(UUID tenantId, NotificationChannel channel, String recipient, String subject, String body) {
        Notification notification = Notification.create(tenantId, channel, recipient, subject, body);

        boolean delivered = notificationSender.send(notification);
        if (delivered) {
            notification.markSent();
        } else {
            notification.markFailed();
        }

        return notificationRepository.save(notification);
    }
}
