package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUserOrderByDateDesc(User user);
    void deleteByUser(User user);

    //New Method to limit 10 activities
    List<UserActivity> findTop10ByUserOrderByDateDesc(User user);
}