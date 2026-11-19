package com.sieve.reminder.dto;

public record ReminderCandidateResponse(
        TaskResponse task,
        Long recipientUserId) {
}
