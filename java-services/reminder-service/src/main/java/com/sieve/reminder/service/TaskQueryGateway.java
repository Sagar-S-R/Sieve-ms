package com.sieve.reminder.service;

import com.sieve.reminder.client.TaskQueryClient;
import com.sieve.reminder.dto.ReminderCandidateResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

@Service
public class TaskQueryGateway {

    private static final Logger log = LoggerFactory.getLogger(TaskQueryGateway.class);

    private final TaskQueryClient taskQueryClient;
    private final CircuitBreaker circuitBreaker;
    private final TimeLimiter timeLimiter;

    public TaskQueryGateway(
            TaskQueryClient taskQueryClient,
            CircuitBreakerRegistry circuitBreakerRegistry,
            TimeLimiterRegistry timeLimiterRegistry) {
        this.taskQueryClient = taskQueryClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("task-query-service");
        this.timeLimiter = timeLimiterRegistry.timeLimiter("task-query-service");
    }

    public List<ReminderCandidateResponse> reminderCandidates(int hours) {
        try {
            Supplier<Future<List<ReminderCandidateResponse>>> futureSupplier =
                    () -> CompletableFuture.supplyAsync(() ->
                            taskQueryClient.reminderCandidates(hours));
            Supplier<Future<List<ReminderCandidateResponse>>> circuitProtected =
                    circuitBreaker.decorateFuture(futureSupplier);
            return timeLimiter.executeFutureSupplier(circuitProtected);
        } catch (Exception exception) {
            Throwable cause = exception;
            if (exception.getCause() != null) {
                cause = exception.getCause();
            }
            log.error("Task-query-service unavailable while fetching {}-hour candidates: {}",
                    hours, cause.getMessage());
            throw new TaskQueryUnavailableException(
                    "query service unavailable",
                    cause instanceof TimeoutException ? cause : exception);
        }
    }
}
