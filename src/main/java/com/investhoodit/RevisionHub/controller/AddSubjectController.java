package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SubjectDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.AddSubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class AddSubjectController {

    private final AddSubjectService addSubjectService;

    public AddSubjectController(AddSubjectService addSubjectService) {
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
}
