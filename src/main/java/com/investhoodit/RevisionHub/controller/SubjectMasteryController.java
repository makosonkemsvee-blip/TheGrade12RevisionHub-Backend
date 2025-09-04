package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SubjectMasteryDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.SubjectMasteryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
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
                        "Subject mastery data retrieved successfully.",
                        true,
                        masteryData
                );
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<List<SubjectMasteryDTO>> response = new ApiResponse<>(
                        "No subject mastery data available.",
                        false,
                        null
                );
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            ApiResponse<List<SubjectMasteryDTO>> response = new ApiResponse<>(
                    "Failed to fetch subject mastery data: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    /*@GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<SubjectMasteryDTO>>> getCourseProgress() {
        try {
            List<SubjectMasteryDTO> courses = subjectMasteryService.getSubjectMastery();
            return ResponseEntity.ok(new ApiResponse<>("Course progress retrieved successfully", true, courses));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Failed to retrieve course progress: " + e.getMessage(), false, null));
        }
    }*/
}