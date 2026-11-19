package com.sieve.reminder.dto;

import java.time.OffsetDateTime;

public record ReminderSentEvent(
        Long taskId,
        Integer level,
        OffsetDateTime sentAt) {
}
