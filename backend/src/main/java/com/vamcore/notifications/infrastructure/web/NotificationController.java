package com.vamcore.notifications.infrastructure.web;

import com.vamcore.notifications.application.dto.NotificationResponse;
import com.vamcore.notifications.application.usecase.ListNotificationsUseCase;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final ListNotificationsUseCase listNotificationsUseCase;

    public NotificationController(ListNotificationsUseCase listNotificationsUseCase) {
        this.listNotificationsUseCase = listNotificationsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('NOTIFICATION_VIEW')")
    public List<NotificationResponse> list() {
        return listNotificationsUseCase.handle().stream().map(NotificationResponse::from).toList();
    }
}
