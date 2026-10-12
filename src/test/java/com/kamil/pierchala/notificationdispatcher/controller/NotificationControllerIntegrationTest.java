package com.kamil.pierchala.notificationdispatcher.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/notifications should return 202 ACCEPTED for valid request")
    void shouldAcceptValidNotification() throws Exception {
        String validJson = """
                {
                    "recipient": "admin@system.pl",
                    "channel": "EMAIL",
                    "title": "System Alert",
                    "content": "Disk space low"
                }
                """;

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.trackingId").isNumber())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.recipient").value("admin@system.pl"))
                .andExpect(jsonPath("$.channel").value("EMAIL"))
                .andExpect(jsonPath("$.submittedAt").exists());
    }

    @Test
    @DisplayName("POST /api/v1/notifications should return 400 BAD REQUEST when validation fails")
    void shouldRejectInvalidNotification() throws Exception {
        String invalidJson = """
                {
                    "recipient": "",
                    "channel": "EMAIL",
                    "title": "",
                    "content": "Some content"
                }
                """;

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.recipient").exists())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    @DisplayName("GET /api/v1/notifications should return list of notification logs")
    void shouldReturnNotificationHistory() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }
}