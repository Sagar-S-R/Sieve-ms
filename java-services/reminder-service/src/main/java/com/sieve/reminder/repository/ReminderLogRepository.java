package com.sieve.reminder.repository;

import com.sieve.reminder.entity.ReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderLogRepository extends JpaRepository<ReminderLog, Long> {
    List<ReminderLog> findAllByOrderBySentAtDesc();
}
