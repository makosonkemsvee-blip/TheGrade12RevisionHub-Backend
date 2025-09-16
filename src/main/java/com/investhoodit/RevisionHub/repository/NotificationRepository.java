package com.investhoodit.RevisionHub.repository;
import com.investhoodit.RevisionHub.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByUserId(Long userId);
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
    void deleteByUserId(Long userId);
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId")
    int updateAllByUserId(@Param("userId") Long userId);

}