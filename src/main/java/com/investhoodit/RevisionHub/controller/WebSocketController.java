package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.Message;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.MessageService;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class WebSocketController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public WebSocketController(MessageService messageService, SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @MessageMapping("/chat/group")
    @Transactional
    public void sendGroupMessage(@Payload Message message) {
        System.out.println("Received group message: " + message.getContent() + ", senderId: " + message.getSenderId());
        if (message.getSenderId() == null) {
            System.err.println("Sender ID is null in group message");
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found: " + message.getSenderId()));
        Message savedMessage = messageService.createMessage(sender.getId(), null, null, message.getContent(), "GROUP");
        messagingTemplate.convertAndSend("/topic/group", savedMessage);
        System.out.println("Sent group message to /topic/group: " + savedMessage.getId());
    }

    @MessageMapping("/chat/group/{groupId}")
    @Transactional
    public void sendGroupMessageToGroup(@Payload Message message, @DestinationVariable Long groupId) {
        System.out.println("Received group message for group " + groupId + ": " + message.getContent() + ", senderId: " + message.getSenderId());
        if (message.getSenderId() == null) {
            System.err.println("Sender ID is null in group message");
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found: " + message.getSenderId()));
        Message savedMessage = messageService.createMessage(sender.getId(), null, groupId, message.getContent(), "GROUP");
        messagingTemplate.convertAndSend("/topic/group/" + groupId, savedMessage);
        System.out.println("Sent group message to /topic/group/" + groupId + ": " + savedMessage.getId());
    }

    @MessageMapping("/chat/private")
    @Transactional
    public void sendPrivateMessage(@Payload Message message) {
        System.out.println("Received private message: " + message.getContent() + ", senderId: " + message.getSenderId() + ", recipientId: " + message.getRecipientId());
        if (message.getSenderId() == null || message.getRecipientId() == null) {
            System.err.println("Sender ID or Recipient ID is null in private message");
            throw new IllegalArgumentException("Sender ID and Recipient ID cannot be null");
        }
        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found: " + message.getSenderId()));
        User recipient = userRepository.findById(message.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found: " + message.getRecipientId()));
        Message savedMessage = messageService.createMessage(sender.getId(), recipient.getId(), null, message.getContent(), "PRIVATE");
        messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue/message", savedMessage);
        messagingTemplate.convertAndSendToUser(recipient.getEmail(), "/queue/message", savedMessage);
        System.out.println("Sent private message to /queue/message: " + savedMessage.getId());
    }
}