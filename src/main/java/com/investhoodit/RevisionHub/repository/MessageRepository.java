package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTypeAndGroupIdIsNull(String type);

    List<Message> findByGroupId(Long groupId);

    @Query("SELECT m FROM Message m WHERE m.type = 'PRIVATE' AND " +
            "((m.senderId = :userId1 AND m.recipientId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.recipientId = :userId1)) " +
            "ORDER BY m.createdAt")
    List<Message> findPrivateMessages(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    List<Message> findBySenderIdAndType(Long senderId, String type);
}