package com.sieve.taskquery.dto;

public record ReminderCandidateResponse(
        TaskResponse task,
        Long recipientUserId) {
}
