package com.sieve.reminder.strategy;

import com.sieve.reminder.dto.TaskResponse;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardReminderStrategyTest {

    private final StandardReminderStrategy strategy = new StandardReminderStrategy();
    private final OffsetDateTime now = OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC);

    @Test
    void selectsTwentyFourHourWindow() {
        TaskResponse task = task(0, now.plusHours(24));

        assertEquals(0, strategy.decide(task, now).orElseThrow().level());
    }

    @Test
    void selectsOneHourWindow() {
        TaskResponse task = task(1, now.plusMinutes(30));

        assertEquals(1, strategy.decide(task, now).orElseThrow().level());
    }

    @Test
    void selectsDeadlineWindow() {
        TaskResponse task = task(2, now.minusMinutes(1));

        assertTrue(strategy.decide(task, now).isPresent());
    }

    private TaskResponse task(int level, OffsetDateTime deadline) {
        return new TaskResponse(1L, null, 99L, null, "Task", "Do it", deadline, level,
                null, "deadline", null, null, null, "standard", now, now);
    }
}
