package com.sieve.taskquery.event;

import com.sieve.taskquery.dto.ReminderSentEvent;
import com.sieve.taskquery.entity.Task;
import com.sieve.taskquery.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderEventListenerTest {

    @Mock
    private TaskRepository taskRepository;

    @Test
    void updatesReminderLevelWhenEventArrives() {
        Task task = new Task();
        when(taskRepository.findById(7L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        new ReminderEventListener(taskRepository).onReminderSent(
                new ReminderSentEvent(
                        7L,
                        2,
                        OffsetDateTime.of(2026, 11, 19, 10, 0, 0, 0, ZoneOffset.UTC)));

        verify(taskRepository).save(task);
        assertEquals(2, task.getReminderLevel());
    }
}
