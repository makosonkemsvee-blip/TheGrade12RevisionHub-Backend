package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SubjectDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;

import com.investhoodit.RevisionHub.model.QuestionPaper;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.service.AddDeleteSubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class AddDeleteSubjectController {

    private final AddDeleteSubjectService addSubjectService;

    public AddDeleteSubjectController(AddDeleteSubjectService addSubjectService) {
        this.addSubjectService = addSubjectService;
    }

    @PostMapping("/add-subject")
    public ResponseEntity<ApiResponse<String>> addSubject(@RequestBody SubjectDTO subjectDTO) {
        try{
            boolean isAdded = addSubjectService.addSubject(subjectDTO);
            if (isAdded) {
                ApiResponse<String> response = new ApiResponse<>(
                        true,
                        "New subject added successfully.",
                        subjectDTO.getSubjectName()
                );
                return ResponseEntity.ok()
                        .body(response);
            }else {
                ApiResponse<String> response = new ApiResponse<>(
                        false,
                        "Subject already exists.",
                        subjectDTO.getSubjectName()
                );
                return ResponseEntity.badRequest()
                        .body(response);
            }
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(
                    false,
                    e.getMessage(),
                    subjectDTO.getSubjectName()
            );
            return ResponseEntity.status(500)
                    .body(response);
        }
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<String>>> subjects(){
        try{
            List<String> subjects = addSubjectService.allSubjects();
            if (!subjects.isEmpty()) {
                ApiResponse<List<String>> response = new ApiResponse<>(
                        true,
                        "Subjects successful found.",
                        subjects
                );
                return ResponseEntity.ok()
                        .body(response);
            }else {
                ApiResponse<List<String>> response = new ApiResponse<>(
                        false,
                        "Error while fetching subjects.",
                        null
                );
                return ResponseEntity.badRequest()
                        .body(response);
            }
        }catch (Exception e) {
            ApiResponse<List<String>> response = new ApiResponse<>(
                    false,
                    (e.getMessage()),
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        }
    }

    @GetMapping("/enrolled-subjects")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> userSubject() {
        try {
            //   log.info("Fetching enrolled subjects for user");
            List<String> subjects = addSubjectService.getAllStudentSubjects();
            ApiResponse<List<String>> response = new ApiResponse<>(
                    true,
                    "Your subjects",
                    subjects
            );
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            //   log.error("Error fetching enrolled subjects", e);
            ApiResponse<List<String>> response = new ApiResponse<>(
                    false,
                    "Failed to fetch subjects: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        }
    }

    @DeleteMapping("/remove-subject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserSubjects>> removeUserSubject(@RequestParam String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            ApiResponse<UserSubjects> response = new ApiResponse<>(
                    false,
                    "Subject name cannot be empty",
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        }
        try {
            boolean removed = addSubjectService.removeSubject(subjectName);
            if (removed) {
                ApiResponse<UserSubjects> response = new ApiResponse<>(
                        true,
                        "Subject removed successfully",
                        null
                );
                return ResponseEntity.ok().body(response);
            } else {
                ApiResponse<UserSubjects> response = new ApiResponse<>(
                        false,
                        "Subject not found or not enrolled",
                        null
                );
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(response);
            }
        } catch (Exception e) {
            //log.error("Error removing subject: {}", subjectName, e);
            ApiResponse<UserSubjects> response = new ApiResponse<>(
                    false,
                    "An error occurred while removing the subject: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}