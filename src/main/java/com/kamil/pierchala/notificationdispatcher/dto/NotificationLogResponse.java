package com.kamil.pierchala.notificationdispatcher.dto;

import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import com.kamil.pierchala.notificationdispatcher.model.NotificationLog;
import com.kamil.pierchala.notificationdispatcher.model.NotificationStatus;

import java.time.Instant;

public record NotificationLogResponse(
        Long id,
        String recipient,
        NotificationChannel channel,
        String title,
        NotificationStatus status,
        String errorMessage,
        Instant createdAt
) {
    public static NotificationLogResponse fromEntity(NotificationLog log) {
        return new NotificationLogResponse(
                log.getId(),
                log.getRecipient(),
                log.getChannel(),
                log.getTitle(),
                log.getStatus(),
                log.getErrorMessage(),
                log.getCreatedAt()
        );
    }
}