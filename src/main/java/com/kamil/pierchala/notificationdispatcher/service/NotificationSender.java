package com.kamil.pierchala.notificationdispatcher.service;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;

public interface NotificationSender {
    void send(NotificationRequest request);
    NotificationChannel supportsChannel();
}