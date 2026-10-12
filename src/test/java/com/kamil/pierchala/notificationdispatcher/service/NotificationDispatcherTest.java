package com.kamil.pierchala.notificationdispatcher.service;

import com.kamil.pierchala.notificationdispatcher.dto.NotificationRequest;
import com.kamil.pierchala.notificationdispatcher.dto.NotificationResponse;
import com.kamil.pierchala.notificationdispatcher.model.NotificationChannel;
import com.kamil.pierchala.notificationdispatcher.model.NotificationLog;
import com.kamil.pierchala.notificationdispatcher.model.NotificationStatus;
import com.kamil.pierchala.notificationdispatcher.repository.NotificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationDispatcherTest {

    @Mock
    private NotificationSender emailSender;

    @Mock
    private NotificationLogRepository logRepository;

    @Mock
    private NotificationAsyncProcessor asyncProcessor;

    private NotificationDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        given(emailSender.supportsChannel()).willReturn(NotificationChannel.EMAIL);
        dispatcher = new NotificationDispatcher(List.of(emailSender), logRepository, asyncProcessor);
    }

    @Test
    @DisplayName("Should successfully dispatch notification and return PENDING response")
    void shouldSuccessfullyDispatchNotification() {
        // given
        NotificationRequest request = new NotificationRequest(
                "user@example.com",
                NotificationChannel.EMAIL,
                "Test Subject",
                "Hello World!"
        );

        NotificationLog savedLog = new NotificationLog(
                request.recipient(),
                request.channel(),
                request.title(),
                request.content(),
                NotificationStatus.PENDING,
                null
        );
        savedLog.setId(100L);

        given(logRepository.save(any(NotificationLog.class))).willReturn(savedLog);

        // when
        NotificationResponse response = dispatcher.dispatch(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.trackingId()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(NotificationStatus.PENDING);
        assertThat(response.channel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(response.recipient()).isEqualTo("user@example.com");

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(captor.getValue().getRecipient()).isEqualTo("user@example.com");

        verify(asyncProcessor).processAsync(emailSender, request, 100L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when channel is unsupported")
    void shouldThrowExceptionWhenChannelIsNotSupported() {
        // given
        NotificationRequest request = new NotificationRequest(
                "https://discord.webhook",
                NotificationChannel.DISCORD,
                "Discord Alert",
                "Message"
        );

        // when & then
        assertThatThrownBy(() -> dispatcher.dispatch(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported notification channel: DISCORD");
    }
}