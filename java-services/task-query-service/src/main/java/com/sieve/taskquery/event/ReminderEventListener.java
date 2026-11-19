package com.sieve.taskquery.event;

import com.sieve.taskquery.dto.ReminderSentEvent;
import com.sieve.taskquery.entity.Task;
import com.sieve.taskquery.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReminderEventListener {

    private static final Logger log = LoggerFactory.getLogger(ReminderEventListener.class);
    private final TaskRepository taskRepository;

    public ReminderEventListener(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "reminder.sent.task-query", durable = "true"),
            exchange = @Exchange(name = "reminder.events", type = "topic", durable = "true"),
            key = "reminder.sent"))
    @Transactional
    public void onReminderSent(ReminderSentEvent event) {
        if (event.level() < 0 || event.level() > 3) {
            log.warn("Ignoring reminder event with invalid level {} for task {}", event.level(), event.taskId());
            return;
        }

        Task task = taskRepository.findById(event.taskId()).orElse(null);
        if (task == null) {
            log.warn("Ignoring reminder event for unknown task {}", event.taskId());
            return;
        }
        task.setReminderLevel(event.level());
        taskRepository.save(task);
        log.info("Updated reminder level for task {} to {}", event.taskId(), event.level());
    }
}
