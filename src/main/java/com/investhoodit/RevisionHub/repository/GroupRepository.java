package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g JOIN GroupMembership gm ON g.id = gm.group.id WHERE gm.user.id = :userId")
    List<Group> findByMemberId(@Param("userId") Long userId);
}