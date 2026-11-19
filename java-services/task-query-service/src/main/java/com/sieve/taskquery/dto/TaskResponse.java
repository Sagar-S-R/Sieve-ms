package com.sieve.taskquery.dto;

import com.sieve.taskquery.entity.Task;

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

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(), task.getGroupId(), task.getUserId(), task.getMessageSenderId(),
                task.getTitle(), task.getActionRequired(), task.getDeadline(), task.getReminderLevel(),
                task.getSourceMessageText(), task.getMessageType(), task.getAppliesAt(), task.getLocation(),
                task.getFormUrl(), task.getReminderStrategy(), task.getCreatedAt(), task.getUpdatedAt());
    }
}
