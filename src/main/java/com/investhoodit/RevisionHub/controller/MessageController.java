package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.GroupDTO;
import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.Group;
import com.investhoodit.RevisionHub.model.Message;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.GroupService;
import com.investhoodit.RevisionHub.service.MessageService;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
/*
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;*/

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/chat")
@Validated
public class MessageController {
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, UserRepository userRepository, GroupService groupService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.groupService = groupService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> users = userRepository.findAll();
        users.removeIf(u -> u.getEmail().equals(currentUserEmail));
        List<UserDTO> userDto = users.stream()
                .map(u -> new UserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(),u.getRole(),u.getCreatedAt(),u.getTwoFactorEnabled()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam("query") @NotBlank String query) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> users = userRepository.searchByFirstNameOrLastName(query);
        users.removeIf(u -> u.getEmail().equals(currentUserEmail));
        List<UserDTO> userDto = users.stream()
                .map(u -> new UserDTO(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(),u.getRole(),u.getCreatedAt(),u.getTwoFactorEnabled()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/group")
    public ResponseEntity<List<Message>> getGroupMessages(@RequestParam(value = "senderId", required = false) Long senderId) {
        if (senderId != null) {
            return ResponseEntity.ok(messageService.getMessagesBySender(senderId, "GROUP"));
        }
        return ResponseEntity.ok(messageService.getGroupMessages());
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupDTO>> getUserGroups() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<GroupDTO> groups = groupService.getUserGroups(currentUser.getId());
        System.out.println("Returning groups for /api/chat/groups: " + groups.stream()
                .map(g -> "Group[id=" + g.getId() + ", name=" + g.getName() + "]")
                .collect(Collectors.joining(", ")));
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Message>> getGroupMessagesByGroupId(@PathVariable @Positive Long groupId, @RequestParam(value = "senderId", required = false) Long senderId) {
        if (senderId != null) {
            List<Message> messages = messageService.getMessagesBySender(senderId, "GROUP");
            messages.removeIf(m -> !m.getGroupId().equals(groupId));
            return ResponseEntity.ok(messages);
        }
        return ResponseEntity.ok(messageService.getGroupMessagesByGroupId(groupId));
    }

    @PostMapping("/group")
    public ResponseEntity<Group> createGroup(@RequestBody @Validated Map<String, Object> groupData) {
        String name = (String) groupData.get("name");
        List<Integer> memberIds = (List<Integer>) groupData.get("memberIds");
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required");
        }
        if (memberIds == null || memberIds.isEmpty()) {
            throw new IllegalArgumentException("At least one member is required");
        }
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Group group = groupService.createGroup(name, currentUser.getId(), memberIds.stream().map(Long::valueOf).collect(Collectors.toList()));
        return ResponseEntity.ok(group);
    }

    @PutMapping("/group/{groupId}")
    public ResponseEntity<GroupDTO> editGroup(@PathVariable @Positive Long groupId, @RequestBody @Validated Map<String, String> groupData) {
        String newName = groupData.get("name");
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name is required");
        }
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        GroupDTO updatedGroup = groupService.editGroup(groupId, newName, currentUser.getId());
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable @Positive Long groupId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        groupService.deleteGroup(groupId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/group/{groupId}/users")
    public ResponseEntity<List<UserDTO>> getGroupUsers(@PathVariable Long groupId, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<UserDTO> users = groupService.getGroupUsers(groupId, user.getId());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/group/{groupId}/users")
    public ResponseEntity<Void> addGroupUser(@PathVariable @Positive Long groupId, @RequestBody @Validated Map<String, Long> userData) {
        Long userId = userData.get("userId");
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Valid user ID is required");
        }
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        groupService.addGroupUser(groupId, userId, currentUser.getId());
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/group/{groupId}/users/{userId}")
    public ResponseEntity<Void> removeGroupUser(@PathVariable @Positive Long groupId, @PathVariable @Positive Long userId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        groupService.removeGroupUser(groupId, userId, currentUser.getId());
        // Fetch updated group users
        List<UserDTO> updatedUsers = groupService.getGroupUsers(groupId, currentUser.getId());
        // Notify group members via WebSocket
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/users", updatedUsers);
        System.out.println("Notified group users for group " + groupId + ": " + updatedUsers.size() + " users");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/private/{otherUserId}")
    public ResponseEntity<List<Message>> getPrivateMessages(@PathVariable String otherUserId, @RequestParam(value = "senderId", required = false) Long senderId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            Long otherId = Long.parseLong(otherUserId);
            if (senderId != null) {
                List<Message> messages = messageService.getMessagesBySender(senderId, "PRIVATE");
                messages.removeIf(m -> !((m.getSenderId().equals(currentUser.getId()) && m.getRecipientId().equals(otherId)) ||
                        (m.getSenderId().equals(otherId) && m.getRecipientId().equals(currentUser.getId()))));
                return ResponseEntity.ok(messages);
            }
            return ResponseEntity.ok(messageService.getPrivateMessages(currentUser.getId(), otherId));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format: " + otherUserId);
        }
    }
}