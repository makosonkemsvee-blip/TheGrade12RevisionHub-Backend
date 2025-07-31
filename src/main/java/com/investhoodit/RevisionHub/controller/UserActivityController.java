package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.UserActivity;
import com.investhoodit.RevisionHub.service.UserActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserActivityController {
    private final UserActivityService activityService;

    public UserActivityController(UserActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/activities")
    public ResponseEntity<ApiResponse<UserActivity>> saveActivity(@RequestBody String description) {
        try {
            UserActivity activity = activityService.saveActivity(description);
            return ResponseEntity.ok(new ApiResponse<>("Activity saved successfully", true, activity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Failed to save activity: " + e.getMessage(), false, null));
        }
    }

    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<List<UserActivity>>> getActivities() {
        try {
            List<UserActivity> activities = activityService.getUserActivities();
            return ResponseEntity.ok(new ApiResponse<>("Activities retrieved successfully", true, activities));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Failed to retrieve activities: " + e.getMessage(), false, null));
        }
    }
}