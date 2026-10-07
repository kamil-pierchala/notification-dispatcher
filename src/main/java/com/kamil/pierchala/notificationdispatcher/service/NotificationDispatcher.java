package com.kamil.pierchala.notificationdispatcher.service;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.dto.NotificationResponse;
import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NotificationDispatcher {

    private final Map<NotificationChannel, NotificationSender> senders;

    public NotificationDispatcher(List<NotificationSender> senderList) {
        this.senders = senderList.stream()
                .collect(Collectors.toMap(NotificationSender::supportsChannel, Function.identity()));
    }

    public NotificationResponse dispatch(NotificationRequest request) {
        NotificationSender sender = Optional.ofNullable(senders.get(request.channel()))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported notification channel: " + request.channel()));

        sender.send(request);

        return new NotificationResponse(
                "SUCCESS",
                request.channel(),
                request.recipient(),
                Instant.now()
        );
    }
}