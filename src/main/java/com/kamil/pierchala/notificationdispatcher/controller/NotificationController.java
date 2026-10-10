package com.kamil.pierchala.notificationdispatcher.controller;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationLogResponse;
import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.dto.NotificationResponse;
import com.kamil.pierchala.notificationdispatcher.service.NotificationDispatcher;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationDispatcher notificationDispatcher;

    public NotificationController(NotificationDispatcher notificationDispatcher) {
        this.notificationDispatcher = notificationDispatcher;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationDispatcher.dispatch(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NotificationLogResponse>> getHistory() {
        return ResponseEntity.ok(notificationDispatcher.getHistory());
    }
}