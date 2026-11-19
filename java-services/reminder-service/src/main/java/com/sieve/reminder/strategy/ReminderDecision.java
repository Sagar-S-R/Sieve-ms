package com.sieve.reminder.strategy;

import java.time.OffsetDateTime;

public record ReminderDecision(int level, String urgency) {

    public boolean appliesTo(OffsetDateTime deadline, OffsetDateTime now) {
        if (deadline == null) {
            return false;
        }
        return switch (level) {
            case 0 -> !deadline.isBefore(now) && !deadline.isAfter(now.plusHours(24));
            case 1 -> !deadline.isBefore(now) && !deadline.isAfter(now.plusHours(1));
            case 2 -> !deadline.isAfter(now);
            default -> false;
        };
    }
}
