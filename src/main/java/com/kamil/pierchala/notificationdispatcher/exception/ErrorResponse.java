package com.kamil.pierchala.notificationdispatcher.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Map<String, String> errors,
        Instant timestamp
) {
}