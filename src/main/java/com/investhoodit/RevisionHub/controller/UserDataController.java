package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.CoursePerformance;
import com.investhoodit.RevisionHub.service.PerformanceOverviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserDataController {

    private final PerformanceOverviewService performanceOverviewService;

    public UserDataController(PerformanceOverviewService performanceOverviewService) {
        this.performanceOverviewService = performanceOverviewService;
    }

    @GetMapping("/performance-overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<CoursePerformance>>> getPerformanceOverview() {
        try {
            List<CoursePerformance> performance = performanceOverviewService.getPerformanceOverview();
            ApiResponse<List<CoursePerformance>> response = new ApiResponse<>(
                    true, "Performance overview fetched successfully", performance
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<List<CoursePerformance>> response = new ApiResponse<>(
                    false, "Failed to fetch performance overview: " + e.getMessage(), null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}