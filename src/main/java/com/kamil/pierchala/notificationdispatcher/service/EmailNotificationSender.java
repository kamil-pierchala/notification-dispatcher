package com.kamil.pierchala.notificationdispatcher.service;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationSender.class);

    @Override
    public void send(NotificationRequest request) {
        log.info("Sending EMAIL to [{}]. Title: '{}', Content: '{}'",
                request.recipient(), request.title(), request.content());
    }

    @Override
    public NotificationChannel supportsChannel() {
        return NotificationChannel.EMAIL;
    }
}