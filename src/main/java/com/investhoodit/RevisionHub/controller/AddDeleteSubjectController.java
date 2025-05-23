package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SubjectDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;

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
    public ResponseEntity<ApiResponse> addSubject(@RequestBody SubjectDTO subjectDTO) {
        try{
            boolean isAdded = addSubjectService.addSubject(subjectDTO);
            if (isAdded) {
                return ResponseEntity.ok()
                        .body(new ApiResponse("New subject added successfully.", true, subjectDTO.getSubjectName()));
            }else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Subject already exists.", false, subjectDTO.getSubjectName()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(e.getMessage(), false, subjectDTO.getSubjectName()));
        }
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse> subjects(){
        try{
            List<String> subjects = addSubjectService.allSubjects();
            if (!subjects.isEmpty()) {
                return ResponseEntity.ok()
                        .body(new ApiResponse("Subjects successful found.", true, subjects));
            }else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Error while fetching subjects.", false, null));
            }
        }catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(e.getMessage(), false, null));
        }
    }

    @GetMapping("/enrolled-subjects")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> userSubject() {
        try {
         //   log.info("Fetching enrolled subjects for user");
            List<String> subjects = addSubjectService.getAllStudentSubjects();
            return ResponseEntity.ok(new ApiResponse("Your subjects", true, subjects));
        } catch (Exception e) {
         //   log.error("Error fetching enrolled subjects", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to fetch subjects: " + e.getMessage(), false, null));
        }
    }

    @DeleteMapping("/remove-subject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> removeUserSubject(@RequestParam String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Subject name cannot be empty", false, null));
        }
        try {
            boolean removed = addSubjectService.removeSubject(subjectName);
            if (removed) {
                return ResponseEntity.ok(new ApiResponse("Subject removed successfully", true, null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("Subject not found or not enrolled", false, null));
            }
        } catch (Exception e) {
            //log.error("Error removing subject: {}", subjectName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while removing the subject: " + e.getMessage(), false, null));
        }
    }
}
