package com.sieve.reminder.service;

import com.sieve.reminder.client.TaskQueryClient;
import com.sieve.reminder.client.TelegramClient;
import com.sieve.reminder.dto.TaskResponse;
import com.sieve.reminder.dto.ReminderCandidateResponse;
import com.sieve.reminder.dto.ReminderSentEvent;
import com.sieve.reminder.dto.ReminderRunResult;
import com.sieve.reminder.repository.ReminderLogRepository;
import com.sieve.reminder.strategy.ReminderStrategy;
import com.sieve.reminder.strategy.ReminderStrategyRegistry;
import com.sieve.reminder.strategy.StandardReminderStrategy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private TaskQueryClient taskQueryClient;

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private ReminderStrategyRegistry strategyRegistry;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ReminderLogRepository reminderLogRepository;

    @Mock
    private TaskQueryGateway taskQueryGateway;

    @Test
    void advancesLevelOnlyAfterSuccessfulDelivery() {
        OffsetDateTime now = OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC);
        TaskResponse task = task(0, now.plusHours(12));
        ReminderStrategy strategy = new StandardReminderStrategy();
        when(strategyRegistry.get("standard")).thenReturn(strategy);
        when(taskQueryGateway.reminderCandidates(24))
                .thenReturn(List.of(new ReminderCandidateResponse(task, 99L)));
        when(taskQueryGateway.reminderCandidates(1)).thenReturn(List.of());
        when(taskQueryGateway.reminderCandidates(0)).thenReturn(List.of());
        when(telegramClient.sendDm(eq(99L), any())).thenReturn(true);

        new ReminderService(
                taskQueryClient,
                telegramClient,
                strategyRegistry,
                rabbitTemplate,
                reminderLogRepository,
                taskQueryGateway).runOnce(now);

        verify(reminderLogRepository).save(any());
        verify(rabbitTemplate).convertAndSend(
                eq("reminder.events"),
                eq("reminder.sent"),
                any(ReminderSentEvent.class));
    }

    @Test
    void doesNotAdvanceLevelWhenDeliveryFails() {
        OffsetDateTime now = OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC);
        TaskResponse task = task(2, now.minusMinutes(1));
        when(strategyRegistry.get("standard")).thenReturn(new StandardReminderStrategy());
        when(taskQueryGateway.reminderCandidates(24)).thenReturn(List.of());
        when(taskQueryGateway.reminderCandidates(1)).thenReturn(List.of());
        when(taskQueryGateway.reminderCandidates(0))
                .thenReturn(List.of(new ReminderCandidateResponse(task, 99L)));
        when(telegramClient.sendDm(eq(99L), any())).thenReturn(false);

        new ReminderService(
                taskQueryClient,
                telegramClient,
                strategyRegistry,
                rabbitTemplate,
                reminderLogRepository,
                taskQueryGateway).runOnce(now);

        verify(reminderLogRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(any(), any(), any(Object.class));
    }

    @Test
    void returnsUnavailableResultWhenTaskQueryServiceIsDown() {
        OffsetDateTime now = OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC);
        doThrow(new TaskQueryUnavailableException("query service unavailable"))
                .when(taskQueryGateway).reminderCandidates(24);

        ReminderRunResult result = new ReminderService(
                taskQueryClient,
                telegramClient,
                strategyRegistry,
                rabbitTemplate,
                reminderLogRepository,
                taskQueryGateway).runOnce(now);

        assertEquals("UNAVAILABLE", result.status());
        assertEquals("query service unavailable", result.message());
        verify(telegramClient, never()).sendDm(any(), any());
    }

    private TaskResponse task(int level, OffsetDateTime deadline) {
        return new TaskResponse(1L, null, 99L, null, "Task", "Do it", deadline, level,
                null, "deadline", null, null, null, "standard", now(), now());
    }

    private OffsetDateTime now() {
        return OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC);
    }
}
