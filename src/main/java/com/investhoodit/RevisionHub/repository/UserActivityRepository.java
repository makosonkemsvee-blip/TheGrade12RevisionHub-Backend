package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUserOrderByDateDesc(User user);
}