package com.sieve.taskquery.controller;

import com.sieve.taskquery.dto.ReminderLevelUpdateRequest;
import com.sieve.taskquery.dto.ReminderCandidateResponse;
import com.sieve.taskquery.dto.TaskResponse;
import com.sieve.taskquery.dto.TaskStatsResponse;
import com.sieve.taskquery.service.TaskQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Read and update existing Sieve tasks")
public class TaskController {

    private final TaskQueryService taskQueryService;

    public TaskController(TaskQueryService taskQueryService) {
        this.taskQueryService = taskQueryService;
    }

    @GetMapping
    @Operation(summary = "Find tasks by group or user")
    public List<TaskResponse> findTasks(
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long userId) {
        return taskQueryService.findTasks(groupId, userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a task by ID")
    public TaskResponse findById(@PathVariable Long id) {
        return taskQueryService.findById(id);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Find tasks due within the requested number of hours")
    public List<TaskResponse> upcoming(@RequestParam(defaultValue = "24") int hours) {
        return taskQueryService.upcoming(hours);
    }

    @GetMapping("/due")
    @Operation(summary = "Find tasks due and not fully reminded")
    public List<TaskResponse> due() {
        return taskQueryService.due();
    }

    @GetMapping("/reminder-candidates")
    @Operation(summary = "Find reminder tasks expanded for their recipients")
    public List<ReminderCandidateResponse> reminderCandidates(
            @RequestParam(defaultValue = "0") int hours) {
        return taskQueryService.reminderCandidates(hours);
    }

    @PatchMapping("/{id}/reminder-level")
    @Operation(summary = "Set a task reminder level")
    public TaskResponse updateReminderLevel(
            @PathVariable Long id,
            @Valid @RequestBody ReminderLevelUpdateRequest request) {
        return taskQueryService.updateReminderLevel(id, request.level());
    }

    @GetMapping("/stats")
    @Operation(summary = "Get task statistics")
    public TaskStatsResponse stats() {
        return taskQueryService.stats();
    }
}
