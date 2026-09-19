package com.vamcore.notifications.application.usecase;

import com.vamcore.notifications.domain.model.Notification;
import com.vamcore.notifications.domain.repository.NotificationRepository;
import com.vamcore.shared.domain.valueobject.TenantId;
import com.vamcore.shared.infrastructure.tenant.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    public ListNotificationsUseCase(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> handle() {
        TenantId tenantId = TenantContext.get();
        return notificationRepository.findAllByTenant(tenantId.value());
    }
}
