package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.UserSignupService;
import com.investhoodit.RevisionHub.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/signup")
public class UserSignupController {

    private final UserSignupService userSignupService;

    public UserSignupController(UserSignupService userSignupService) {
        this.userSignupService = userSignupService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody UserDTO userDTO) {
        try {
            User createdUser = userSignupService.signUp(userDTO);
            if (createdUser != null) {
                return ResponseEntity.status(201)
                        .body(new ApiResponse("User created successfully", true, createdUser));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Email already exist.", false, null));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Invalid input: " + e.getMessage(), false, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("Signup failed: " + e.getMessage(), false, null));
        }
    }
}