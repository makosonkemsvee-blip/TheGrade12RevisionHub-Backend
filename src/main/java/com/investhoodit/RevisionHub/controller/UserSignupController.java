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
    public ResponseEntity<ApiResponse<User>> signUp(@Valid @RequestBody UserDTO userDTO) {
        try {
            User createdUser = userSignupService.signUp(userDTO);
            if (createdUser != null) {
                ApiResponse<User> response = new ApiResponse<>(
                        true,
                        "User created successfully",
                        createdUser
                );
                return ResponseEntity.status(201)
                        .body(response);
            } else {
                ApiResponse<User> response = new ApiResponse<>(
                        false,
                        "Email already exist.",
                        null
                );
                return ResponseEntity.badRequest()
                        .body(response);
            }
        } catch (IllegalArgumentException e) {
            ApiResponse<User> response = new ApiResponse<>(
                    false,
                    "Invalid input: " + e.getMessage(),
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(
                    false,
                    "Signup failed: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(500)
                    .body(response);
        }
    }
}