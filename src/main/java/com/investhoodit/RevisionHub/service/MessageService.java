package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Message;
import com.investhoodit.RevisionHub.repository.MessageRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository repository;

    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public Message createMessage(Long senderId, Long recipientId, String content, String type) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setContent(content);
        message.setType(type);
        message.setCreatedAt(LocalDateTime.now());
        return repository.save(message);
    }

    public List<Message> getGroupMessages() {
        return repository.findByTypeOrderByCreatedAtAsc("GROUP");
    }

    public List<Message> getPrivateMessages(Long userId1, Long userId2) {
        return repository.findBySenderIdAndRecipientIdOrRecipientIdAndSenderIdOrderByCreatedAtAsc(
                userId1, userId2, userId2, userId1
        );
    }


}
