package com.sieve.taskquery.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "message_sender_id")
    private Long messageSenderId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(name = "action_required")
    private String actionRequired;

    private OffsetDateTime deadline;

    @Column(name = "reminder_level")
    private Integer reminderLevel;

    @Column(name = "source_message_text")
    private String sourceMessageText;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "applies_at")
    private OffsetDateTime appliesAt;

    private String location;

    @Column(name = "form_url")
    private String formUrl;

    @Column(name = "reminder_strategy")
    private String reminderStrategy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Long getId() { return id; }
    public Long getGroupId() { return groupId; }
    public Long getUserId() { return userId; }
    public Long getMessageSenderId() { return messageSenderId; }
    public String getTitle() { return title; }
    public String getActionRequired() { return actionRequired; }
    public OffsetDateTime getDeadline() { return deadline; }
    public Integer getReminderLevel() { return reminderLevel; }
    public String getSourceMessageText() { return sourceMessageText; }
    public String getMessageType() { return messageType; }
    public OffsetDateTime getAppliesAt() { return appliesAt; }
    public String getLocation() { return location; }
    public String getFormUrl() { return formUrl; }
    public String getReminderStrategy() { return reminderStrategy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setReminderLevel(Integer reminderLevel) { this.reminderLevel = reminderLevel; }
}
