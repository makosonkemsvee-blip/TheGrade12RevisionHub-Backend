package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SubjectDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.service.AddDeleteSubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class SubjectController {

    private final AddDeleteSubjectService addDeleteSubjectService;

    public SubjectController(AddDeleteSubjectService addDeleteSubjectService) {
        this.addDeleteSubjectService = addDeleteSubjectService;
    }

    @PostMapping("/add-subject")
    public ResponseEntity<ApiResponse<String>> addSubject(@RequestBody SubjectDTO subjectDTO) {
        try {
            boolean isAdded = addDeleteSubjectService.addSubject(subjectDTO);
            if (isAdded) {
                ApiResponse<String> response = new ApiResponse<>(
                        "New subject added successfully.",
                        true,
                        subjectDTO.getSubjectName()
                );
                return ResponseEntity.status(201).body(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(
                        "Subject already exists.",
                        false,
                        subjectDTO.getSubjectName()
                );
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(
                    e.getMessage(),
                    false,
                    subjectDTO.getSubjectName()
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<String>>> subjects() {
        try {
            List<String> subjects = addDeleteSubjectService.allSubjects();
            if (!subjects.isEmpty()) {
                ApiResponse<List<String>> response = new ApiResponse<>(
                        "Subjects successfully found.",
                        true,
                        subjects
                );
                return ResponseEntity.ok().body(response);
            } else {
                ApiResponse<List<String>> response = new ApiResponse<>(
                        "Error while fetching subjects.",
                        false,
                        null
                );
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            ApiResponse<List<String>> response = new ApiResponse<>(
                    e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/enrolled-subjects")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> userSubject() {
        try {
            List<String> subjects = addDeleteSubjectService.getAllStudentSubjects();
            ApiResponse<List<String>> response = new ApiResponse<>(
                    "Your subjects",
                    true,
                    subjects
            );
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ApiResponse<List<String>> response = new ApiResponse<>(
                    "Failed to fetch subjects: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/remove-subject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserSubjects>> removeUserSubject(@RequestParam String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            ApiResponse<UserSubjects> response = new ApiResponse<>(
                    "Subject name cannot be empty",
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
        try {
            boolean removed = addDeleteSubjectService.removeSubject(subjectName);
            if (removed) {
                ApiResponse<UserSubjects> response = new ApiResponse<>(
                        "Subject removed successfully",
                        true,
                        null
                );
                return ResponseEntity.ok().body(response);
            } else {
                ApiResponse<UserSubjects> response = new ApiResponse<>(
                        "Subject not found or not enrolled",
                        false,
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            ApiResponse<UserSubjects> response = new ApiResponse<>(
                    "An error occurred while removing the subject: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}