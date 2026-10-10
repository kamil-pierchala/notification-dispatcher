package com.kamil.pierchala.notificationdispatcher.repository;

import com.kamil.pierchala.notificationdispatcher.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findAllByOrderByCreatedAtDesc();
}