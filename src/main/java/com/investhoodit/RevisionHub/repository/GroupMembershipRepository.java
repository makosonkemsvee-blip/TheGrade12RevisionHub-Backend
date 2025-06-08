package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.user.id = :userId")
    List<GroupMembership> findByUserId(@Param("userId") Long userId);

    @Query("DELETE FROM GroupMembership gm WHERE gm.group.id = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);
}