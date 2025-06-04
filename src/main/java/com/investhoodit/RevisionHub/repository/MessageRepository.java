package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTypeOrderByCreatedAtAsc(String type); // Group messages
    List<Message> findBySenderIdAndRecipientIdOrRecipientIdAndSenderIdOrderByCreatedAtAsc(
            Long senderId, Long recipientId, Long recipientId2, Long senderId2
    ); // Private messages
}
