package com.kamil.pierchala.notificationdispatcher.service;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationLogResponse;
import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.dto.NotificationResponse;
import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import com.kamil.pierchala.notificationdispatcher.model.NotificationLog;
import com.kamil.pierchala.notificationdispatcher.model.NotificationStatus;
import com.kamil.pierchala.notificationdispatcher.repository.NotificationLogRepository;
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
    private final NotificationLogRepository logRepository;
    private final NotificationAsyncProcessor asyncProcessor;

    public NotificationDispatcher(
            List<NotificationSender> senderList,
            NotificationLogRepository logRepository,
            NotificationAsyncProcessor asyncProcessor) {
        this.senders = senderList.stream()
                .collect(Collectors.toMap(NotificationSender::supportsChannel, Function.identity()));
        this.logRepository = logRepository;
        this.asyncProcessor = asyncProcessor;
    }

    public NotificationResponse dispatch(NotificationRequest request) {
        NotificationSender sender = Optional.ofNullable(senders.get(request.channel()))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported notification channel: " + request.channel()));

        NotificationLog pendingLog = new NotificationLog(
                request.recipient(),
                request.channel(),
                request.title(),
                request.content(),
                NotificationStatus.PENDING,
                null
        );
        NotificationLog savedLog = logRepository.save(pendingLog);

        asyncProcessor.processAsync(sender, request, savedLog.getId());

        return new NotificationResponse(
                savedLog.getId(),
                NotificationStatus.PENDING,
                request.channel(),
                request.recipient(),
                Instant.now()
        );
    }

    public List<NotificationLogResponse> getHistory() {
        return logRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(NotificationLogResponse::fromEntity)
                .toList();
    }
}