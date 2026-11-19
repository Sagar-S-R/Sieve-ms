package com.sieve.reminder.controller;

import com.sieve.reminder.service.ReminderService;
import com.sieve.reminder.dto.ReminderRunResult;
import com.sieve.reminder.entity.ReminderLog;
import com.sieve.reminder.repository.ReminderLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@Tag(name = "Reminders", description = "Reminder delivery operations")
public class ReminderController {

    private final ReminderService reminderService;
    private final ReminderLogRepository reminderLogRepository;

    public ReminderController(
            ReminderService reminderService,
            ReminderLogRepository reminderLogRepository) {
        this.reminderService = reminderService;
        this.reminderLogRepository = reminderLogRepository;
    }

    @PostMapping("/run")
    @Operation(summary = "Run the standard reminder sweep immediately")
    public ResponseEntity<ReminderRunResult> run() {
        ReminderRunResult result =
                reminderService.runOnce(OffsetDateTime.now(ZoneOffset.UTC));
        if ("UNAVAILABLE".equals(result.status())) {
            return ResponseEntity.status(503).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/log")
    @Operation(summary = "List reminder delivery logs")
    public List<ReminderLog> log() {
        return reminderLogRepository.findAllByOrderBySentAtDesc();
    }
}
