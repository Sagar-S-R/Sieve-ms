package com.sieve.reminder.strategy;

import com.sieve.reminder.dto.TaskResponse;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface ReminderStrategy {
    Optional<ReminderDecision> decide(TaskResponse task, OffsetDateTime now);
}
