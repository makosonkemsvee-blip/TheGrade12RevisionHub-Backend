package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SubjectMasteryDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.UserPaperPerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class PerformanceCountController {
    private final UserPaperPerformanceService performanceService;

    public PerformanceCountController(UserPaperPerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping("/completed-tasks")
    public ResponseEntity<ApiResponse<Long>> getCompletedTasksCount() {
        try {
            long count = performanceService.getCompletedTasksCount();
            return ResponseEntity.ok(new ApiResponse<>("Completed tasks count retrieved successfully", true, count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Failed to retrieve completed tasks count: " + e.getMessage(), false, null));
        }
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<SubjectMasteryDTO>>> getSubjectProgress() {
        try {
            List<SubjectMasteryDTO> progress = performanceService.getSubjectProgress();
            return ResponseEntity.ok(new ApiResponse<>("Subject progress retrieved successfully", true, progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Failed to retrieve subject progress: " + e.getMessage(), false, null));
        }
    }
}