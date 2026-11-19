package com.sieve.taskquery.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReminderLevelUpdateRequest(
        @NotNull @Min(0) @Max(3) Integer level) {
}
