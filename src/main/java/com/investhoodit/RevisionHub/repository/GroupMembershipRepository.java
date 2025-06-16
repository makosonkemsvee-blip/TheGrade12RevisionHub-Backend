package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {
    @Query("SELECT gm FROM GroupMembership gm WHERE gm.user.id = :userId")
    List<GroupMembership> findByUserId(@Param("userId") Long userId);

    @Query("SELECT gm FROM GroupMembership gm JOIN FETCH gm.user WHERE gm.group.id = :groupId")
    List<GroupMembership> findByGroupId(Long groupId);

    /*del group*/
    @Modifying
    @Query("DELETE FROM GroupMembership gm WHERE gm.group.id = :groupId")
    void deleteByGroupId(@Param("groupId") Long groupId);

    /**/
    @Query("SELECT gm.user.id FROM GroupMembership gm WHERE gm.group.id = :groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId);

    /*select*/
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.user.id = :userId")
    boolean existsByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);


    /*delete from a group*/
    @Modifying
    @Query("DELETE FROM GroupMembership gm WHERE gm.group.id = :groupId AND gm.user.id = :userId")
    void deleteByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

}