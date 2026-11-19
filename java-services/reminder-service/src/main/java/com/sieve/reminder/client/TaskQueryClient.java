package com.sieve.reminder.client;

import com.sieve.reminder.dto.ReminderLevelUpdateRequest;
import com.sieve.reminder.dto.ReminderCandidateResponse;
import com.sieve.reminder.dto.TaskResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "task-query-service")
public interface TaskQueryClient {

    @GetMapping("/api/tasks/upcoming")
    List<TaskResponse> upcoming(@RequestParam("hours") int hours);

    @GetMapping("/api/tasks/due")
    List<TaskResponse> due();

    @GetMapping("/api/tasks/reminder-candidates")
    List<ReminderCandidateResponse> reminderCandidates(@RequestParam("hours") int hours);

    @PatchMapping("/api/tasks/{id}/reminder-level")
    TaskResponse updateReminderLevel(
            @PathVariable("id") Long id,
            @RequestBody ReminderLevelUpdateRequest request);
}
