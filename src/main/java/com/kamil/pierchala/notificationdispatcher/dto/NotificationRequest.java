package com.kamil.pierchala.notificationdispatcher.dto;

import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

    public record NotificationRequest(
            @NotBlank(message = "Recipient cannot be blank")
            String recipient,

            @NotNull(message = "Notification channel is required")
            NotificationChannel channel,

            @NotBlank(message = "Title cannot be blank")
            String title,

            @NotBlank(message = "Content cannot be blank")
            String content
    ) {
    }
