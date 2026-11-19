package com.sieve.taskquery.service;

import com.sieve.taskquery.annotation.AuditedService;
import com.sieve.taskquery.annotation.TimedExecution;
import com.sieve.taskquery.dto.TaskResponse;
import com.sieve.taskquery.dto.ReminderCandidateResponse;
import com.sieve.taskquery.dto.TaskStatsResponse;
import com.sieve.taskquery.entity.Task;
import com.sieve.taskquery.repository.GroupSubscriptionRepository;
import com.sieve.taskquery.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AuditedService
public class TaskQueryService {

    private final TaskRepository taskRepository;
    private final GroupSubscriptionRepository groupSubscriptionRepository;

    public TaskQueryService(
            TaskRepository taskRepository,
            GroupSubscriptionRepository groupSubscriptionRepository) {
        this.taskRepository = taskRepository;
        this.groupSubscriptionRepository = groupSubscriptionRepository;
    }

    @TimedExecution
    @Transactional(readOnly = true)
    public List<TaskResponse> findTasks(Long groupId, Long userId) {
        if (groupId != null) {
            return taskRepository.findByGroupId(groupId).stream().map(TaskResponse::from).toList();
        }
        if (userId != null) {
            return taskRepository.findByUserId(userId).stream().map(TaskResponse::from).toList();
        }
        return taskRepository.findAll().stream().map(TaskResponse::from).toList();
    }

    @TimedExecution
    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return taskRepository.findById(id).map(TaskResponse::from)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @TimedExecution
    @Transactional(readOnly = true)
    public List<TaskResponse> upcoming(int hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("hours must be greater than zero");
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return taskRepository.findDueBefore(now.plusHours(hours).toLocalDateTime()).stream()
                .filter(task -> task.getDeadline() == null || !task.getDeadline().isBefore(now))
                .map(TaskResponse::from)
                .toList();
    }

    @TimedExecution
    @Transactional(readOnly = true)
    public List<TaskResponse> due() {
        return taskRepository.findByReminderLevelLessThanAndDeadlineBefore(3, OffsetDateTime.now(ZoneOffset.UTC))
                .stream().map(TaskResponse::from).toList();
    }

    @TimedExecution
    @Transactional(readOnly = true)
    public List<ReminderCandidateResponse> reminderCandidates(int hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("hours must not be negative");
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<Task> tasks = taskRepository.findByReminderLevelLessThanAndDeadlineBefore(
                3, now.plusHours(hours));

        return tasks.stream()
                .flatMap(task -> recipientsFor(task).stream()
                        .map(recipient -> new ReminderCandidateResponse(
                                TaskResponse.from(task), recipient)))
                .toList();
    }

    private List<Long> recipientsFor(Task task) {
        if (task.getGroupId() != null) {
            return groupSubscriptionRepository.findSubscriberIdsByGroupId(task.getGroupId());
        }
        return task.getUserId() == null ? List.of() : List.of(task.getUserId());
    }

    @TimedExecution
    @Transactional(readOnly = true)
    public TaskStatsResponse stats() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        return new TaskStatsResponse(
                taskRepository.count(),
                taskRepository.countByDeadlineAfter(now),
                taskRepository.countByDeadlineBefore(now),
                taskRepository.countByReminderLevel(0),
                taskRepository.countByReminderLevel(1),
                taskRepository.countByReminderLevel(2),
                taskRepository.countByReminderLevel(3));
    }
}
