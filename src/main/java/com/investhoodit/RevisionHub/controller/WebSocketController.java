package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.GroupUsersUpdateDTO;
import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.Message;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.MessageService;
import com.investhoodit.RevisionHub.service.NotificationService;
import com.investhoodit.RevisionHub.service.GroupService;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Controller
public class WebSocketController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final GroupService groupService;

    public WebSocketController(MessageService messageService, SimpMessagingTemplate messagingTemplate,
                               UserRepository userRepository, NotificationService notificationService,
                               GroupService groupService) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.groupService = groupService;
    }

    @MessageMapping("/chat/group")
    @Transactional
    public void sendGroupMessage(@Payload Message message) {
        System.out.println("Received group message: " + message.getContent() + ", senderId: " + message.getSenderId());
        if (message.getSenderId() == null) {
            System.err.println("Sender ID is null in group message");
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        Long senderId = Long.valueOf(message.getSenderId());
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        Message savedMessage = messageService.createMessage(sender.getId(), null, null, message.getContent(), "GROUP");
        savedMessage.setSenderName(sender.getFirstName() + " " + sender.getLastName());
        savedMessage.setMessageSnippet(message.getContent().length() > 7 ? message.getContent().substring(0, 7) + "..." : message.getContent());

        // Broadcast message
        messagingTemplate.convertAndSend("/topic/group", savedMessage);
        System.out.println("Sent group message to /topic/group: " + savedMessage.getId());

        // Create notifications for all users (excluding sender)
        userRepository.findAll().forEach(user -> {
            if (!user.getId().equals(sender.getId())) {
                notificationService.createNotification(
                        user.getId(),
                        "New group message from " + savedMessage.getSenderName() + ": " + savedMessage.getMessageSnippet(),
                        "chat"
                );
            }
        });
    }

    @MessageMapping("/chat/group/{groupId}")
    @Transactional
    public void sendGroupMessageToGroup(@Payload Message message, @DestinationVariable Long groupId) {
        System.out.println("Received group message for group " + groupId + ": " + message.getContent() + ", senderId: " + message.getSenderId());
        if (message.getSenderId() == null) {
            System.err.println("Sender ID is null in group message");
            throw new IllegalArgumentException("Sender ID cannot be null");
        }
        Long senderId = Long.valueOf(message.getSenderId());
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        Message savedMessage = messageService.createMessage(sender.getId(), null, groupId, message.getContent(), "GROUP");
        savedMessage.setSenderName(sender.getFirstName() + " " + sender.getLastName());
        savedMessage.setMessageSnippet(message.getContent().length() > 7 ? message.getContent().substring(0, 7) + "..." : message.getContent());

        // Fetch group name
        final String groupName = groupService.getGroupName(groupId) != null ? groupService.getGroupName(groupId) : "Unnamed Group";

        // Broadcast message
        messagingTemplate.convertAndSend("/topic/group/" + groupId, savedMessage);
        System.out.println("Sent group message to /topic/group/" + groupId + ": " + savedMessage.getId());

        // Create notifications for group members except sender
        List<UserDTO> members = groupService.getGroupMembers(groupId);
        members.stream()
                .filter(member -> !member.getId().equals(sender.getId()))
                .forEach(member -> notificationService.createNotification(
                        member.getId(),
                        "New message in \"" + groupName + "\" group from " + savedMessage.getSenderName() + ": " + savedMessage.getMessageSnippet(),
                        "chat"
                ));
    }

    @MessageMapping("/chat/private")
    @Transactional
    public void sendPrivateMessage(@Payload Message message) {
        System.out.println("Received private message: " + message.getContent() + ", senderId: " + message.getSenderId() + ", recipientId: " + message.getRecipientId());
        if (message.getSenderId() == null || message.getRecipientId() == null) {
            System.err.println("Sender ID or Recipient ID is null in private message");
            throw new IllegalArgumentException("Sender ID and Recipient ID cannot be null");
        }
        Long senderId = Long.valueOf(message.getSenderId());
        Long recipientId = Long.valueOf(message.getRecipientId());
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientId));
        Message savedMessage = messageService.createMessage(sender.getId(), recipient.getId(), null, message.getContent(), "PRIVATE");
        savedMessage.setSenderName(sender.getFirstName() + " " + sender.getLastName());
        savedMessage.setMessageSnippet(message.getContent().length() > 7 ? message.getContent().substring(0, 7) + "..." : message.getContent());

        // Broadcast message to both users
        messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue/message", savedMessage);
        messagingTemplate.convertAndSendToUser(recipient.getEmail(), "/queue/message", savedMessage);
        System.out.println("Sent private message to /queue/message: " + savedMessage.getId());

        // Create notification for recipient
        notificationService.createNotification(
                recipient.getId(),
                "New private message from " + savedMessage.getSenderName() + ": " + savedMessage.getMessageSnippet(),
                "chat"
        );
    }

    @MessageMapping("/group/{groupId}/users")
    public void updateGroupUsers(@DestinationVariable Long groupId, @Payload GroupUsersUpdateDTO update) {
        System.out.println("Received group users update for group " + groupId + ": " + update.getUsers().size() + " users");
        // Broadcast updated users to subscribers
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/users", update.getUsers());
        System.out.println("Notified group users for group " + groupId + ": " + update.getUsers().size() + " users");
    }
}