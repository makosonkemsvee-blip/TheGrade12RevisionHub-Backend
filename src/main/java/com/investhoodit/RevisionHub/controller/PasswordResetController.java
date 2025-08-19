package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.ApiResponse;
import com.investhoodit.RevisionHub.dto.OtpVerificationDTO;
import com.investhoodit.RevisionHub.dto.PasswordResetDTO;
import com.investhoodit.RevisionHub.service.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<ApiResponse<String>> requestPasswordReset(@PathVariable String email) {
        logger.info("Received request to send OTP for email: {}", email);
        try {
            String message = passwordResetService.sendPasswordResetEmail(email);
            logger.info("OTP sent for email: {}, message: {}", email, message);
            return ResponseEntity.ok(new ApiResponse<>(true, null, message));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid email: {}, error: {}", email, e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error sending OTP for email: {}, error: {}", email, e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Failed to send OTP"));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody OtpVerificationDTO otpVerificationDTO) {
        logger.info("Verifying OTP: {} for email: {}", otpVerificationDTO.getOtp(), otpVerificationDTO.getEmail());
        try {
            boolean isValid = passwordResetService.verifyOtp(otpVerificationDTO.getEmail(), otpVerificationDTO.getOtp());
            if (isValid) {
                logger.info("OTP verified successfully for email: {}", otpVerificationDTO.getEmail());
                return ResponseEntity.ok(new ApiResponse<>(true, null, "OTP verified successfully"));
            } else {
                logger.warn("Invalid or expired OTP for email: {}", otpVerificationDTO.getEmail());
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid or expired OTP"));
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid OTP or email: {}, error: {}", otpVerificationDTO.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error verifying OTP for email: {}, error: {}", otpVerificationDTO.getEmail(), e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Server error during OTP verification"));
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        logger.info("Resetting password for email: {}", passwordResetDTO.getEmail());
        try {
            String message = passwordResetService.resetPassword(passwordResetDTO);
            logger.info("Password reset successful for email: {}", passwordResetDTO.getEmail());
            return ResponseEntity.ok(new ApiResponse<>(true, null, message));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid reset data for email: {}, error: {}", passwordResetDTO.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            logger.error("Error resetting password for email: {}, error: {}", passwordResetDTO.getEmail(), e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse<>(false, null, "Password reset failed"));
        }
    }
}