package com.sieve.taskquery.repository;

import com.sieve.taskquery.entity.GroupSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupSubscriptionRepository extends JpaRepository<GroupSubscription, Long> {

    @Query("select s.subscriberId from GroupSubscription s where s.groupId = :groupId")
    List<Long> findSubscriberIdsByGroupId(@Param("groupId") Long groupId);
}
