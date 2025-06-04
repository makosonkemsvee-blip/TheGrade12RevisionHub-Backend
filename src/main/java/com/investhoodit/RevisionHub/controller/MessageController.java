package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.Message;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.MessageService;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class MessageController {
    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> users = userRepository.findAll();
        users.removeIf(u -> u.getEmail().equals(currentUserEmail)); // Exclude current user
        return ResponseEntity.ok(users);
    }

    @GetMapping("/group")
    public ResponseEntity<List<Message>> getGroupMessages() {
        return ResponseEntity.ok(messageService.getGroupMessages());
    }

    @GetMapping("/private/{otherUserId}")
    public ResponseEntity<List<Message>> getPrivateMessages(@PathVariable String otherUserId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            Long otherId = Long.parseLong(otherUserId);
            return ResponseEntity.ok(messageService.getPrivateMessages(currentUser.getId(), otherId));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format: " + otherUserId);
        }
    }
}
