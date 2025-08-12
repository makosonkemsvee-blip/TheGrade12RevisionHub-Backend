package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.UserSignupService;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.OTPRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserSignupController {
    private final UserSignupService userSignupService;

    public UserSignupController(UserSignupService userSignupService) {
        this.userSignupService = userSignupService;
    }

    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<User>> signUp(@Valid @RequestBody UserDTO userDTO) {
        try {
            User createdUser = userSignupService.signUp(userDTO);
            ApiResponse<User> response = new ApiResponse<>(
                    "User created successfully. Please verify your email with the OTP sent.",
                    true,
                    createdUser
            );
            return ResponseEntity.status(201).body(response);
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(
                    "Signup failed: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOTP(@RequestBody OTPRequest otpRequest) {
        boolean isVerified = userSignupService.verifyOTP(otpRequest.getEmail(), otpRequest.getOtp());
        if (isVerified) {
            ApiResponse<String> response = new ApiResponse<>(
                    "Email verified successfully",
                    true,
                    null
            );
            return ResponseEntity.ok().body(response);
        } else {
            ApiResponse<String> response = new ApiResponse<>(
                    "Invalid or expired OTP",
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse<String>> resendOTP(@RequestBody OTPRequest request) {
        try {
            String message = userSignupService.resendOTP(request.getEmail());
            ApiResponse<String> response = new ApiResponse<>(
                    message,
                    message.contains("successfully"),
                    request.getEmail()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(
                    "Failed to resend OTP: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }
    }
}