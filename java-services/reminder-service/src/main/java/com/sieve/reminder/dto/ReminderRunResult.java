package com.sieve.reminder.dto;

public record ReminderRunResult(
        int processed,
        String status,
        String message) {
}
