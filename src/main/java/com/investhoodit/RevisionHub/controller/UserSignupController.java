package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.UserSignupService;
import com.investhoodit.RevisionHub.model.User;
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
            //return ResponseEntity.ok(new ApiResponse<>("Email verified successfully", true, null));
        } else {
            ApiResponse<String> response = new ApiResponse<>(
                    "Invalid or expired OTP",
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
            //return ResponseEntity.badRequest(new ApiResponse<>("Invalid or expired OTP", false, null));
        }
    }
}

// DTO for OTP verification request
class OTPRequest {
    private String email;
    private String otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}