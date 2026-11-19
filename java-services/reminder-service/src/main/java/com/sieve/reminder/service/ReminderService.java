package com.sieve.reminder.service;

import com.sieve.reminder.annotation.AuditedService;
import com.sieve.reminder.annotation.TimedExecution;
import com.sieve.reminder.client.TaskQueryClient;
import com.sieve.reminder.client.TelegramClient;
import com.sieve.reminder.dto.ReminderCandidateResponse;
import com.sieve.reminder.dto.ReminderSentEvent;
import com.sieve.reminder.dto.ReminderRunResult;
import com.sieve.reminder.dto.TaskResponse;
import com.sieve.reminder.entity.ReminderLog;
import com.sieve.reminder.repository.ReminderLogRepository;
import com.sieve.reminder.strategy.ReminderDecision;
import com.sieve.reminder.strategy.ReminderStrategy;
import com.sieve.reminder.strategy.ReminderStrategyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AuditedService
public class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private final TaskQueryClient taskQueryClient;
    private final TelegramClient telegramClient;
    private final ReminderStrategyRegistry strategyRegistry;
    private final RabbitTemplate rabbitTemplate;
    private final ReminderLogRepository reminderLogRepository;
    private final TaskQueryGateway taskQueryGateway;

    public ReminderService(
            TaskQueryClient taskQueryClient,
            TelegramClient telegramClient,
            ReminderStrategyRegistry strategyRegistry,
            RabbitTemplate rabbitTemplate,
            ReminderLogRepository reminderLogRepository,
            TaskQueryGateway taskQueryGateway) {
        this.taskQueryClient = taskQueryClient;
        this.telegramClient = telegramClient;
        this.strategyRegistry = strategyRegistry;
        this.rabbitTemplate = rabbitTemplate;
        this.reminderLogRepository = reminderLogRepository;
        this.taskQueryGateway = taskQueryGateway;
    }

    @Scheduled(fixedDelayString = "${reminder.interval-ms:60000}")
    public void scheduledRun() {
        runOnce(OffsetDateTime.now(ZoneOffset.UTC));
    }

    @TimedExecution
    @Transactional
    public ReminderRunResult runOnce(OffsetDateTime now) {
        ReminderStrategy strategy = strategyRegistry.get("standard");
        Map<String, ReminderCandidateResponse> tasks = new HashMap<>();
        try {
            addTasks(tasks, taskQueryGateway.reminderCandidates(24));
            addTasks(tasks, taskQueryGateway.reminderCandidates(1));
            addTasks(tasks, taskQueryGateway.reminderCandidates(0));
        } catch (TaskQueryUnavailableException exception) {
            log.error("POST /api/reminders/run cannot query due tasks: {}",
                    exception.getMessage(), exception);
            return new ReminderRunResult(
                    0,
                    "UNAVAILABLE",
                    "query service unavailable");
        }

        int processed = 0;
        for (ReminderCandidateResponse candidate : tasks.values()) {
            TaskResponse task = candidate.task();
            ReminderDecision decision = strategy.decide(task, now).orElse(null);
            if (decision == null) {
                continue;
            }

            String message = formatMessage(task, decision);
            if (telegramClient.sendDm(candidate.recipientUserId(), message)) {
                int nextLevel = decision.level() + 1;
                OffsetDateTime sentAt = OffsetDateTime.now(ZoneOffset.UTC);
                reminderLogRepository.save(
                        new ReminderLog(task.id(), nextLevel, sentAt, "SENT"));
                rabbitTemplate.convertAndSend(
                        "reminder.events",
                        "reminder.sent",
                        new ReminderSentEvent(task.id(), nextLevel, sentAt));
                processed++;
            } else {
                log.error("Reminder delivery failed; level not advanced for task {}", task.id());
            }
        }
        return new ReminderRunResult(processed, "OK", "Reminder sweep completed");
    }

    private void addTasks(Map<String, ReminderCandidateResponse> tasks,
                          List<ReminderCandidateResponse> incoming) {
        incoming.forEach(candidate -> tasks.putIfAbsent(
                candidate.task().id() + ":" + candidate.recipientUserId(), candidate));
    }

    private String formatMessage(TaskResponse task, ReminderDecision decision) {
        String deadline = task.deadline() == null ? "unknown" : task.deadline().toString();
        return "<b>" + decision.urgency() + "</b>\n\n"
                + "<b>" + task.title() + "</b>\n"
                + (task.actionRequired() == null ? "" : task.actionRequired()) + "\n"
                + "Deadline: " + deadline;
    }
}
