package com.kamil.pierchala.notificationdispatcher.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "notification_logs")
@Getter
@Setter
@NoArgsConstructor
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    private String errorMessage;

    @Column(nullable = false)
    private Instant createdAt;

    public NotificationLog(String recipient, NotificationChannel channel, String title, String content, NotificationStatus status, String errorMessage) {
        this.recipient = recipient;
        this.channel = channel;
        this.title = title;
        this.content = content;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = Instant.now();
    }
}