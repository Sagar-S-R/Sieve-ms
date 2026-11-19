package com.sieve.taskquery.dto;

public record TaskStatsResponse(
        long total,
        long upcoming,
        long overdue,
        long reminderLevelZero,
        long reminderLevelOne,
        long reminderLevelTwo,
        long reminderLevelThree) {
}
