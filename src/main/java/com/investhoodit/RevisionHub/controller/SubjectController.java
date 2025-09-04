package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SubjectDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.Subject;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.repository.SubjectRepository;
import com.investhoodit.RevisionHub.service.AddDeleteSubjectService;
import com.investhoodit.RevisionHub.service.DataMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class SubjectController {

    private static final Logger logger = LoggerFactory.getLogger(SubjectController.class);

    //private final AddDeleteSubjectService addSubjectService;
    private final DataMigrationService dataMigrationService;
    private final AddDeleteSubjectService addDeleteSubjectService;
    private final SubjectRepository subjectRepository;

    public SubjectController(AddDeleteSubjectService addDeleteSubjectService, DataMigrationService dataMigrationService, SubjectRepository subjectRepository) {
        this.addDeleteSubjectService = addDeleteSubjectService;
        this.dataMigrationService = dataMigrationService;
        this.subjectRepository = subjectRepository;
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

    @GetMapping("/api/subjects")
    public ResponseEntity<ApiResponse<List<String>>> getSubjects() {
        try {
            List<String> subjects = addDeleteSubjectService.allSubjects();
            if (!subjects.isEmpty()) {
                ApiResponse<List<String>> response = new ApiResponse<>(
                        "Subjects retrieved successfully",
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

//    @GetMapping("/api/subjects")
//    public ResponseEntity<ApiResponse<List<Subject>>> getSubjects() {
//        try {
//            List<Subject> subjects = subjectRepository.findAll();
//            if (!subjects.isEmpty()) {
//                ApiResponse<List<Subject>> response = new ApiResponse<>(
//                        "Subjects retrieved successfully",
//                        true,
//                        subjects
//                );
//                return ResponseEntity.ok().body(response);
//            } else {
//                ApiResponse<List<Subject>> response = new ApiResponse<>(
//                        "Error while fetching subjects.",
//                        false,
//                        null
//                );
//                return ResponseEntity.badRequest().body(response);
//            }
//        } catch (Exception e) {
//            ApiResponse<List<Subject>> response = new ApiResponse<>(
//                    e.getMessage(),
//                    false,
//                    null
//            );
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

//    @GetMapping("/api/subjects")
//    public ResponseEntity<ApiResponse<List<Subject>>> getSubjects() {
//        try {
//            List<Subject> subjects = subjectRepository.findAll();
//            logger.info("Fetched {} subjects from /api/subjects", subjects.size());
//            ApiResponse<List<Subject>> response = new ApiResponse<>(
//                    "Subjects retrieved successfully",
//                    true,
//                    subjects
//            );
//            return ResponseEntity.ok().body(response);
//        } catch (Exception e) {
//            logger.error("Error retrieving subjects from /api/subjects: {}", e.getMessage(), e);
//            ApiResponse<List<Subject>> response = new ApiResponse<>(
//                    "Error retrieving subjects: " + e.getMessage(),
//                    false,
//                    null
//            );
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

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