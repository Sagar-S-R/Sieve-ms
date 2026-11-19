package com.sieve.taskquery.dto;

import java.time.OffsetDateTime;

public record ReminderSentEvent(
        Long taskId,
        Integer level,
        OffsetDateTime sentAt) {
}
