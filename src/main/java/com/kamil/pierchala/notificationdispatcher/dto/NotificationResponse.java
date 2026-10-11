package com.kamil.pierchala.notificationdispatcher.dto;

import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import com.kamil.pierchala.notificationdispatcher.model.NotificationStatus;

import java.time.Instant;

public record NotificationResponse(
        Long trackingId,
        NotificationStatus status,
        NotificationChannel channel,
        String recipient,
        Instant submittedAt
) {
}

