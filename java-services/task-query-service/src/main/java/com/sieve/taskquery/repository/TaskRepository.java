package com.sieve.taskquery.repository;

import com.sieve.taskquery.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t where t.groupId = :groupId order by t.deadline asc nulls last")
    List<Task> findByGroupId(@Param("groupId") Long groupId);

    @Query("select t from Task t where t.userId = :userId order by t.deadline asc nulls last")
    List<Task> findByUserId(@Param("userId") Long userId);

    @Query("select t from Task t where t.deadline <= :deadline order by t.deadline asc")
    List<Task> findDueBeforeAt(@Param("deadline") OffsetDateTime deadline);

    default List<Task> findDueBefore(LocalDateTime deadline) {
        return findDueBeforeAt(deadline.atOffset(java.time.ZoneOffset.UTC));
    }

    @Query("""
            select t from Task t
            where t.reminderLevel < :level and t.deadline <= :deadline
            order by t.deadline asc
            """)
    List<Task> findByReminderLevelLessThanAndDeadlineBefore(
            @Param("level") Integer level,
            @Param("deadline") OffsetDateTime deadline);

    long countByReminderLevel(Integer reminderLevel);

    long countByDeadlineAfter(OffsetDateTime time);

    long countByDeadlineBefore(OffsetDateTime time);
}
