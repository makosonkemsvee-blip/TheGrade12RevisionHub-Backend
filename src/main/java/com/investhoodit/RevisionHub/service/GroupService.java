package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.GroupDTO;
import com.investhoodit.RevisionHub.model.Group;
import com.investhoodit.RevisionHub.model.GroupMembership;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.GroupMembershipRepository;
import com.investhoodit.RevisionHub.repository.GroupRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.hibernate.Hibernate;
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

    public GroupService(GroupRepository groupRepository, GroupMembershipRepository groupMembershipRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.userRepository = userRepository;
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
    public List<User> getGroupMembers(Long groupId){
        List<GroupMembership> memberships = groupMembershipRepository.findByGroupId(groupId);
        return memberships.stream()
                .map(GroupMembership::getUser)
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
    }
    public String getGroupName(Long groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        return group.map(Group::getName).orElse("Unnamed Group"); // Fallback if not found
    }
}