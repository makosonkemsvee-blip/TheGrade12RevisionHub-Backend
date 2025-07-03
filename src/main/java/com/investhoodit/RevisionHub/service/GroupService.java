package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.GroupDTO;
import com.investhoodit.RevisionHub.dto.GroupUsersUpdateDTO;
import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.Group;
import com.investhoodit.RevisionHub.model.GroupMembership;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.GroupMembershipRepository;
import com.investhoodit.RevisionHub.repository.GroupRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public GroupService(GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository,
                        UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Group createGroup(String name, Long creatorId, List<Long> memberIds) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));
        Group group = new Group();
        group.setName(name);
        group.setCreatedAt(LocalDateTime.now());
        group.setCreatorId(creatorId);
        group = groupRepository.save(group);

        // Add creator to group
        GroupMembership creatorMembership = new GroupMembership();
        creatorMembership.setGroup(group);
        creatorMembership.setUser(creator);
        groupMembershipRepository.save(creatorMembership);

        // Add other members
        for (Long memberId : memberIds) {
            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found: " + memberId));
            GroupMembership membership = new GroupMembership();
            membership.setGroup(group);
            membership.setUser(member);
            groupMembershipRepository.save(membership);
        }

        // Notify group users
        notifyGroupUsers(group.getId());
        return group;
    }

    @Transactional(readOnly = true)
    public List<GroupDTO> getUserGroups(Long userId) {
        List<GroupMembership> memberships = groupMembershipRepository.findByUserId(userId);
        List<GroupDTO> groups = memberships.stream()
                .map(GroupMembership::getGroup)
                .map(group -> {
                    Hibernate.initialize(group);
                    return new GroupDTO(group.getId(), group.getName(), group.getCreatedAt(), group.getCreatorId());
                })
                .collect(Collectors.toList());
        System.out.println("Returning groups for user " + userId + ": " + groups.stream()
                .map(g -> "Group[id=" + g.getId() + ", name=" + g.getName() + "]")
                .collect(Collectors.joining(", ")));
        return groups;
    }

    @Transactional
    public GroupDTO editGroup(Long groupId, String newName, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));
        if (!group.getCreatorId().equals(userId)) {
            throw new RuntimeException("Only the group creator can edit the group");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new RuntimeException("Group name cannot be empty");
        }
        group.setName(newName);
        group = groupRepository.save(group);
        return new GroupDTO(group.getId(), group.getName(), group.getCreatedAt(), group.getCreatorId());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getGroupMembers(Long groupId) {
        List<GroupMembership> memberships = groupMembershipRepository.findByGroupId(groupId);
        return memberships.stream()
                .map(membership -> {
                    var user = membership.getUser();
                    return new UserDTO(
                            user.getId(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getRole(),
                            user.getCreatedAt(), // Provide createdAt from User
                            user.getTwoFactorEnabled() // Provide twoFactorEnabled from User
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));
        if (!group.getCreatorId().equals(userId)) {
            throw new RuntimeException("Only the group creator can delete the group");
        }
        groupMembershipRepository.deleteByGroupId(groupId);
        groupRepository.deleteById(groupId);
        // Notify group users
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/users", List.of());
    }

    public String getGroupName(Long groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        return group.map(Group::getName).orElse("Unnamed Group");
    }

    @Transactional
    public void addGroupUser(Long groupId, Long userId, Long currentUserId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));
        if (!group.getCreatorId().equals(currentUserId)) {
            throw new RuntimeException("Only the group creator can add users");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        if (groupMembershipRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("User is already a member of the group");
        }
        GroupMembership membership = new GroupMembership();
        membership.setGroup(group);
        membership.setUser(user);
        groupMembershipRepository.save(membership);
        notifyGroupUsers(groupId);
    }

    @Transactional
    public void removeGroupUser(Long groupId, Long userId, Long currentUserId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));
        if (!group.getCreatorId().equals(currentUserId)) {
            throw new RuntimeException("Only the group creator can remove users");
        }
        if (!groupMembershipRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        groupMembershipRepository.deleteByGroupIdAndUserId(groupId, userId);
        notifyGroupUsers(groupId);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getGroupUsers(Long groupId, Long requesterId) {
        if (!groupMembershipRepository.existsByGroupIdAndUserId(groupId, requesterId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        return getGroupMembers(groupId);
    }

    private void notifyGroupUsers(Long groupId) {
        List<UserDTO> users = getGroupMembers(groupId);
        GroupUsersUpdateDTO update = new GroupUsersUpdateDTO(users);
        messagingTemplate.convertAndSend("/app/group/" + groupId + "/users", update);
        System.out.println("Notified group users for group " + groupId + ": " + users.size() + " users");
    }
}