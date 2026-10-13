package com.kamil.pierchala.notificationdispatcher.exception;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(Long id) {
        super("Notification log with id " + id + " not found");
    }
}