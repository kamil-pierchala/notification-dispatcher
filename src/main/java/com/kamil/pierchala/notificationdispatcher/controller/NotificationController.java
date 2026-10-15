package com.kamil.pierchala.notificationdispatcher.controller;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationLogResponse;
import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.dto.NotificationResponse;
import com.kamil.pierchala.notificationdispatcher.service.NotificationDispatcher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Endpoints for dispatching notifications and tracking delivery status")
public class NotificationController {

    private final NotificationDispatcher dispatcher;

    public NotificationController(NotificationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping
    @Operation(summary = "Dispatch a new notification asynchronously", description = "Validates the request, creates a PENDING log entry, and queues the notification for background delivery.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Notification accepted for asynchronous processing"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation errors")
    })
    public ResponseEntity<NotificationResponse> dispatch(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(dispatcher.dispatch(request));
    }

    @GetMapping
    @Operation(summary = "Get notification history", description = "Fetches all notification logs ordered by creation timestamp descending.")
    @ApiResponse(responseCode = "200", description = "List of notification logs successfully retrieved")
    public ResponseEntity<List<NotificationLogResponse>> getHistory() {
        return ResponseEntity.ok(dispatcher.getHistory());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by tracking ID", description = "Fetches a specific notification log by its database ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification log found"),
            @ApiResponse(responseCode = "404", description = "Notification log not found")
    })
    public ResponseEntity<NotificationLogResponse> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(dispatcher.getById(id));
    }
}