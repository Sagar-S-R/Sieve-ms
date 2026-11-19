package com.sieve.reminder.dto;

import java.time.OffsetDateTime;

public record TaskResponse(
        Long id,
        Long groupId,
        Long userId,
        Long messageSenderId,
        String title,
        String actionRequired,
        OffsetDateTime deadline,
        Integer reminderLevel,
        String sourceMessageText,
        String messageType,
        OffsetDateTime appliesAt,
        String location,
        String formUrl,
        String reminderStrategy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
