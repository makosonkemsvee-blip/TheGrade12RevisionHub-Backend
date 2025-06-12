package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.Message;
import com.investhoodit.RevisionHub.repository.MessageRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Message createMessage(Long senderId, Long recipientId, Long groupId, String content, String type) {
        userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        if (recipientId != null) {
            userRepository.findById(recipientId)
                    .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientId));
        }
        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setGroupId(groupId);
        message.setContent(content);
        message.setType(type);
        message.setCreatedAt(LocalDateTime.now());
        System.out.println("Creating message: " + content + ", type: " + type + ", groupId: " + groupId);
        Message savedMessage = messageRepository.save(message);
        System.out.println("Saved message with ID: " + savedMessage.getId());
        return savedMessage;
    }

    public List<Message> getGroupMessages() {
        return messageRepository.findByTypeAndGroupIdIsNull("GROUP");
    }

    public List<Message> getGroupMessagesByGroupId(Long groupId) {
        return messageRepository.findByGroupId(groupId);
    }

    public List<Message> getPrivateMessages(Long userId1, Long userId2) {
        return messageRepository.findPrivateMessages(userId1, userId2);
    }

    public List<Message> getMessagesBySender(Long senderId, String type) {
        return messageRepository.findBySenderIdAndType(senderId, type);
    }
}