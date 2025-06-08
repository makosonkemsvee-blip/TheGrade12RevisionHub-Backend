package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.GroupDTO;
import com.investhoodit.RevisionHub.model.Group;
import com.investhoodit.RevisionHub.model.Message;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.GroupService;
import com.investhoodit.RevisionHub.service.MessageService;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class MessageController {
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final GroupService groupService;

    public MessageController(MessageService messageService, UserRepository userRepository, GroupService groupService) {
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.groupService = groupService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> users = userRepository.findAll();
        users.removeIf(u -> u.getEmail().equals(currentUserEmail));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("query") String query) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<User> users = userRepository.searchByFirstNameOrLastName(query);
        users.removeIf(u -> u.getEmail().equals(currentUserEmail));
        return ResponseEntity.ok(users);
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
    public ResponseEntity<List<Message>> getGroupMessagesByGroupId(@PathVariable Long groupId, @RequestParam(value = "senderId", required = false) Long senderId) {
        if (senderId != null) {
            List<Message> messages = messageService.getMessagesBySender(senderId, "GROUP");
            messages.removeIf(m -> !m.getGroupId().equals(groupId));
            return ResponseEntity.ok(messages);
        }
        return ResponseEntity.ok(messageService.getGroupMessagesByGroupId(groupId));
    }

    @PostMapping("/group")
    public ResponseEntity<Group> createGroup(@RequestBody Map<String, Object> groupData) {
        String name = (String) groupData.get("name");
        List<Integer> memberIds = (List<Integer>) groupData.get("memberIds");
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Group group = groupService.createGroup(name, currentUser.getId(), memberIds.stream().map(Long::valueOf).collect(Collectors.toList()));
        return ResponseEntity.ok(group);
    }

    //edit
    @PutMapping("/group/{groupId}")
    public ResponseEntity<GroupDTO> editGroup(@PathVariable Long groupId, @RequestBody Map<String, String> groupData) {
        String newName = groupData.get("name");
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        GroupDTO updatedGroup = groupService.editGroup(groupId, newName, currentUser.getId());
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long groupId) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        groupService.deleteGroup(groupId, currentUser.getId());
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