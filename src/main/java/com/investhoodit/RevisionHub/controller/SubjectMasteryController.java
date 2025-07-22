package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SubjectMasteryDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.SubjectMasteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class SubjectMasteryController {

    private final SubjectMasteryService subjectMasteryService;

    public SubjectMasteryController(SubjectMasteryService subjectMasteryService) {
        this.subjectMasteryService = subjectMasteryService;
    }

    @GetMapping("/subject-mastery")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SubjectMasteryDTO>>> getSubjectMastery() {
        try {
            List<SubjectMasteryDTO> masteryData = subjectMasteryService.getSubjectMastery();
            if (!masteryData.isEmpty()) {
                ApiResponse<List<SubjectMasteryDTO>> response = new ApiResponse<>(
                        "Subject mastery data retrieved successfully.", // message first
                        true, // success second
                        masteryData // data third
                );
                return ResponseEntity.ok().body(response);
            } else {
                ApiResponse<List<SubjectMasteryDTO>> response = new ApiResponse<>(
                        "No mastery data available.", // message first
                        false, // success second
                        null // data third
                );
                return ResponseEntity.ok().body(response);
            }
        } catch (Exception e) {
            ApiResponse<List<SubjectMasteryDTO>> response = new ApiResponse<>(
                    "Failed to fetch subject mastery data: " + e.getMessage(), // message first
                    false, // success second
                    null // data third
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}