package com.sieve.reminder.strategy;

import com.sieve.reminder.annotation.ReminderStrategyType;
import com.sieve.reminder.dto.TaskResponse;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@ReminderStrategyType("standard")
public class StandardReminderStrategy implements ReminderStrategy {

    @Override
    public Optional<ReminderDecision> decide(TaskResponse task, OffsetDateTime now) {
        if (task.deadline() == null || task.reminderLevel() == null || task.reminderLevel() >= 3) {
            return Optional.empty();
        }
        int level = task.reminderLevel();
        String urgency = switch (level) {
            case 0 -> "24-Hour Warning";
            case 1 -> "1-Hour Final Call";
            case 2 -> "DEADLINE NOW";
            default -> "";
        };
        ReminderDecision decision = new ReminderDecision(level, urgency);
        return decision.appliesTo(task.deadline(), now) ? Optional.of(decision) : Optional.empty();
    }
}
