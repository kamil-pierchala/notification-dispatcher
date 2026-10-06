package com.kamil.pierchala.notificationdispatcher.dto;

import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import java.time.Instant;

public record NotificationResponse(
        String status,
        NotificationChannel channel,
        String recipient,
        Instant timestamp
) {
}

