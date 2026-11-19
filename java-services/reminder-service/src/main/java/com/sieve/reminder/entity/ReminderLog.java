package com.sieve.reminder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "reminder_logs")
public class ReminderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(nullable = false)
    private Integer level;

    @Column(name = "sent_at", nullable = false)
    private OffsetDateTime sentAt;

    @Column(nullable = false)
    private String status;

    protected ReminderLog() {
    }

    public ReminderLog(Long taskId, Integer level, OffsetDateTime sentAt, String status) {
        this.taskId = taskId;
        this.level = level;
        this.sentAt = sentAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Integer getLevel() {
        return level;
    }

    public OffsetDateTime getSentAt() {
        return sentAt;
    }

    public String getStatus() {
        return status;
    }
}
