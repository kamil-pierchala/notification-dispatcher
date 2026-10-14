package com.kamil.pierchala.notificationdispatcher.service;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class DiscordNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(DiscordNotificationSender.class);

    private final RestClient restClient;

    public DiscordNotificationSender(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public void send(NotificationRequest request) {
        String webhookUrl = request.recipient();
        log.info("Sending Discord notification to webhook: {}", webhookUrl);

        // Formatting message using Discord's markdown
        String formattedMessage = "**%s**\n%s".formatted(request.title(), request.content());
        Map<String, String> payload = Map.of("content", formattedMessage);

        restClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Discord notification successfully delivered!");
    }

    @Override
    public NotificationChannel supportsChannel() {
        return NotificationChannel.DISCORD;
    }
}