package com.sieve.taskquery.controller;

import com.sieve.taskquery.dto.TaskResponse;
import com.sieve.taskquery.service.TaskQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskQueryService taskQueryService;

    @Test
    void returnsTasksForGroup() throws Exception {
        when(taskQueryService.findTasks(12L, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/tasks").param("groupId", "12"))
                .andExpect(status().isOk());
    }

    @Test
    void returnsBadRequestForInvalidReminderLevel() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/tasks/1/reminder-level")
                        .contentType("application/json")
                        .content("{\"level\": 9}"))
                .andExpect(status().isBadRequest());
    }
}
