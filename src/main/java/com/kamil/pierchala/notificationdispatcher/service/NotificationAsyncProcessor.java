package com.kamil.pierchala.notificationdispatcher.service;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.model.NotificationLog;
import com.kamil.pierchala.notificationdispatcher.model.NotificationStatus;
import com.kamil.pierchala.notificationdispatcher.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public void processAsync(NotificationSender sender, NotificationRequest request, Long logId) {
        log.info("Processing notification asynchronously for task [{}] in thread: {}",
                logId, Thread.currentThread().getName());

        NotificationLog logEntry = logRepository.findById(logId).orElse(null);
        if (logEntry == null) {
            log.error("Could not find log entry for id: {}", logId);
            return;
        }

        try {
            // Simulation of network transmission time (e.g. 1.5s) to clearly demonstrate the asynchronous nature
            Thread.sleep(1500);

            sender.send(request);
            logEntry.setStatus(NotificationStatus.SENT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage("Dispatch interrupted");
        } catch (Exception ex) {
            log.error("Failed to send notification for task [{}]: {}", logId, ex.getMessage());
            logEntry.setStatus(NotificationStatus.FAILED);
            logEntry.setErrorMessage(ex.getMessage());
        } finally {
            logRepository.save(logEntry);
        }
    }
}