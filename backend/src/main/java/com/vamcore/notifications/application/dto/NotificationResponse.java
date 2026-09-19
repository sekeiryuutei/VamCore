package com.vamcore.notifications.application.dto;

import com.vamcore.notifications.domain.model.Notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID id, String channel, String recipient, String subject, String body,
    String status, Instant createdAt, Instant sentAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
            n.id(), n.channel().name(), n.recipient(), n.subject(), n.body(),
            n.status().name(), n.createdAt(), n.sentAt()
        );
    }
}
