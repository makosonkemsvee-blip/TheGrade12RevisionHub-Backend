package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserActivity;
import com.investhoodit.RevisionHub.repository.UserActivityRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityService {
    private static final Logger log = LoggerFactory.getLogger(UserActivityService.class);
    private final UserActivityRepository activityRepository;
    private final UserRepository userRepository;

    public UserActivityService(UserActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    public UserActivity saveActivity(String description) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });

        UserActivity activity = new UserActivity();
        activity.setUser(user);
        activity.setDescription(description);
        activity.setDate(LocalDateTime.now());
        log.info("Saving activity for user {}: {}", email, description);
        return activityRepository.save(activity);
    }

    public List<UserActivity> getUserActivities() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });
        List<UserActivity> activities = activityRepository.findByUserOrderByDateDesc(user);
        log.info("Retrieved {} activities for user {}", activities.size(), email);
        return activities;
    }
}