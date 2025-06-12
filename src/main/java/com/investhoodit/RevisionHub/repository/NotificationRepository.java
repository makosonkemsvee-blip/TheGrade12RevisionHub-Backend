package com.investhoodit.RevisionHub.repository;
import com.investhoodit.RevisionHub.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}