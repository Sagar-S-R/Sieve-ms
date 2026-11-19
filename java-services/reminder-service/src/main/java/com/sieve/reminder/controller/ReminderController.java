package com.sieve.reminder.controller;

import com.sieve.reminder.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
@Tag(name = "Reminders", description = "Reminder delivery operations")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping("/run")
    @Operation(summary = "Run the standard reminder sweep immediately")
    public ResponseEntity<Map<String, Integer>> run() {
        return ResponseEntity.ok(Map.of(
                "processed", reminderService.runOnce(OffsetDateTime.now(ZoneOffset.UTC))));
    }
}
