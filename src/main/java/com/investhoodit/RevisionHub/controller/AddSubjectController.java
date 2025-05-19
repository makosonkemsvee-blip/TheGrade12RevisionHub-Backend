package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.AddSubjectDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.UserSubjects;
import com.investhoodit.RevisionHub.repository.UserSubjectsRepository;
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
    public ResponseEntity<ApiResponse> addSubject(@RequestBody AddSubjectDTO dto) {
        try{
            boolean isAdded = addSubjectService.addSubject(dto);
            System.out.println(isAdded);
            if (isAdded) {
                return ResponseEntity.status(201)
                        .body(new ApiResponse("New subject added successfully.", true, dto));
            }else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Subject already exists.", false, dto));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse(e.getMessage(), false, dto));
        }

    }
}
