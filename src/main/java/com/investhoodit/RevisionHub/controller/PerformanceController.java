package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.PerformanceDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.PerformanceService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/user")
public class PerformanceController {
    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<Page<PerformanceDTO>>> getPerformance(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            LocalDate parsedStartDate = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate parsedEndDate = endDate != null ? LocalDate.parse(endDate) : null;

            Page<PerformanceDTO> performancePage = performanceService.getPerformanceByFilters(
                    userId,
                    subjectName,
                    activityType,
                    parsedStartDate,
                    parsedEndDate,
                    page,
                    size
            );
            ApiResponse<Page<PerformanceDTO>> response = new ApiResponse<>(
                    true,
                    "Performance data retrieved successfully",
                    performancePage
            );
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            ApiResponse<Page<PerformanceDTO>> response = new ApiResponse<>(
                    false,
                    "Invalid date format: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<Page<PerformanceDTO>> response = new ApiResponse<>(
                    false,
                    "An error occurred while retrieving performance data: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }
}