package com.kamil.pierchala.notificationdispatcher.service;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.model.NotificationLog;
import com.kamil.pierchala.notificationdispatcher.model.NotificationStatus;
import com.kamil.pierchala.notificationdispatcher.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationAsyncProcessor {

    private static final Logger log = LoggerFactory.getLogger(NotificationAsyncProcessor.class);

    private final NotificationLogRepository logRepository;

    public NotificationAsyncProcessor(NotificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Async("notificationTaskExecutor")
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public void processAsync(NotificationSender sender, NotificationRequest request, Long logId) {
        log.info("Attempting notification delivery for task [{}] via [{}] on thread [{}]",
                logId, request.channel(), Thread.currentThread().getName());

        try {
            // Simulation of network transmission time (e.g. 1.5s) to clearly demonstrate the asynchronous nature
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Dispatch interrupted during simulation", e);
        }

        sender.send(request);

        NotificationLog logEntry = logRepository.findById(logId).orElse(null);
        if (logEntry != null) {
            logEntry.setStatus(NotificationStatus.SENT);
            logEntry.setErrorMessage(null);
            logRepository.save(logEntry);
            log.info("Notification task [{}] marked as SENT", logId);
        }
    }

    @Recover
    public void recover(Exception ex, NotificationSender sender, NotificationRequest request, Long logId) {
        log.error("All retry attempts exhausted for task [{}]. Delivery failed: {}", logId, ex.getMessage());

        NotificationLog logEntry = logRepository.findById(logId).orElse(null);
        if (logEntry != null) {
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage("Retries exhausted: " + ex.getMessage());
            logRepository.save(logEntry);
        }
    }
}