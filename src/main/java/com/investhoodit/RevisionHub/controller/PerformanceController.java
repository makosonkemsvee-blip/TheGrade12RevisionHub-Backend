package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.PerformanceRequest;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserPaperPerformance;
import com.investhoodit.RevisionHub.service.PerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class PerformanceController {
    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordPerformance(
            @RequestBody PerformanceRequest request) {

        Map<String, Object> response = new HashMap<>();
        try {
            User user = performanceService.findByToken();

            // Process request
            UserPaperPerformance performance = performanceService.recordAttempt(
                    user.getId(),
                    request.getPaperId(),
                    request.getScore(),
                    request.getMaxScore()
            );
            response.put("success", true);
            response.put("data", performance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/performance/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPerformance(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserPaperPerformance> performances = performanceService.getUserPerformance(userId);
            response.put("success", true);
            response.put("data", performances);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}