package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.PerformanceDTO;
import com.investhoodit.RevisionHub.dto.PerformanceRequest;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.PerformanceMetric;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserPaperPerformance;
import com.investhoodit.RevisionHub.service.PerformanceService;
import com.investhoodit.RevisionHub.service.UserPaperPerformanceService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserPaperPerformanceController {
    private final UserPaperPerformanceService performanceService;
    private final PerformanceService perfService;

    public UserPaperPerformanceController(UserPaperPerformanceService performanceService, PerformanceService perfService) {
        this.performanceService = performanceService;
        this.perfService = perfService;
    }

    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordPerformance(
            @RequestBody PerformanceRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            User user = performanceService.findByToken();

            // Process request
            PerformanceMetric performance = performanceService.recordAttempt(request);
            response.put("success", true);
            response.put("data", performance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

//    @GetMapping("/performance")
//    public ResponseEntity<Map<String, Object>> getUserPerformance() {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            List<UserPaperPerformance> performances = performanceService.getUserPerformance();
//            response.put("success", true);
//            response.put("data", performances);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

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

            Page<PerformanceDTO> performancePage = perfService.getPerformanceByFilters(
                    userId,
                    subjectName,
                    activityType,
                    parsedStartDate,
                    parsedEndDate,
                    page,
                    size
            );
            ApiResponse<Page<PerformanceDTO>> response = new ApiResponse<>(
                    "Performance data retrieved successfully",
                    true,
                    performancePage
            );
            return ResponseEntity.ok(response);
        } catch (DateTimeParseException e) {
            ApiResponse<Page<PerformanceDTO>> response = new ApiResponse<>(
                    "Invalid date format: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<Page<PerformanceDTO>> response = new ApiResponse<>(
                    "An error occurred while retrieving performance data: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }
}