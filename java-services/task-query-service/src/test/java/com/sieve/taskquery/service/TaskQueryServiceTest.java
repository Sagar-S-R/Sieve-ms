package com.sieve.taskquery.service;

import com.sieve.taskquery.entity.Task;
import com.sieve.taskquery.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskQueryServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private com.sieve.taskquery.repository.GroupSubscriptionRepository groupSubscriptionRepository;

    @InjectMocks
    private TaskQueryService taskQueryService;

    @Test
    void findsTaskByIdWithoutExposingEntity() {
        Task task = new Task();
        when(taskRepository.findById(7L)).thenReturn(Optional.of(task));

        assertInstanceOf(com.sieve.taskquery.dto.TaskResponse.class, taskQueryService.findById(7L));
    }

    @Test
    void rejectsNonPositiveUpcomingWindow() {
        assertThrows(IllegalArgumentException.class, () -> taskQueryService.upcoming(0));
    }

    @Test
    void rejectsMissingTask() {
        when(taskRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskQueryService.findById(7L));
    }

}
