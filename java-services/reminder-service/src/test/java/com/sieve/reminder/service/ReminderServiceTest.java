package com.sieve.reminder.service;

import com.sieve.reminder.client.TaskQueryClient;
import com.sieve.reminder.client.TelegramClient;
import com.sieve.reminder.dto.TaskResponse;
import com.sieve.reminder.dto.ReminderCandidateResponse;
import com.sieve.reminder.strategy.ReminderStrategy;
import com.sieve.reminder.strategy.ReminderStrategyRegistry;
import com.sieve.reminder.strategy.StandardReminderStrategy;
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

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private TaskQueryClient taskQueryClient;

    @Mock
    private TelegramClient telegramClient;

    @Mock
    private ReminderStrategyRegistry strategyRegistry;

    @Test
    void advancesLevelOnlyAfterSuccessfulDelivery() {
        OffsetDateTime now = OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC);
        TaskResponse task = task(0, now.plusHours(12));
        ReminderStrategy strategy = new StandardReminderStrategy();
        when(strategyRegistry.get("standard")).thenReturn(strategy);
        when(taskQueryClient.reminderCandidates(24))
                .thenReturn(List.of(new ReminderCandidateResponse(task, 99L)));
        when(taskQueryClient.reminderCandidates(1)).thenReturn(List.of());
        when(taskQueryClient.reminderCandidates(0)).thenReturn(List.of());
        when(telegramClient.sendDm(eq(99L), any())).thenReturn(true);

        new ReminderService(taskQueryClient, telegramClient, strategyRegistry).runOnce(now);

        verify(taskQueryClient).updateReminderLevel(1L,
                new com.sieve.reminder.dto.ReminderLevelUpdateRequest(1));
    }

    @Test
    void doesNotAdvanceLevelWhenDeliveryFails() {
        OffsetDateTime now = OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC);
        TaskResponse task = task(2, now.minusMinutes(1));
        when(strategyRegistry.get("standard")).thenReturn(new StandardReminderStrategy());
        when(taskQueryClient.reminderCandidates(24)).thenReturn(List.of());
        when(taskQueryClient.reminderCandidates(1)).thenReturn(List.of());
        when(taskQueryClient.reminderCandidates(0))
                .thenReturn(List.of(new ReminderCandidateResponse(task, 99L)));
        when(telegramClient.sendDm(eq(99L), any())).thenReturn(false);

        new ReminderService(taskQueryClient, telegramClient, strategyRegistry).runOnce(now);

        verify(taskQueryClient, never()).updateReminderLevel(any(), any());
    }

    private TaskResponse task(int level, OffsetDateTime deadline) {
        return new TaskResponse(1L, null, 99L, null, "Task", "Do it", deadline, level,
                null, "deadline", null, null, null, "standard", now(), now());
    }

    private OffsetDateTime now() {
        return OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC);
    }
}
