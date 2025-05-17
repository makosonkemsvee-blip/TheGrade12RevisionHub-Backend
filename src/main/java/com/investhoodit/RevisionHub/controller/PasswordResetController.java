package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.PasswordResetDTO;
import com.investhoodit.RevisionHub.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/send-otp/{email}")
    public ResponseEntity<String> requestPasswordReset(@PathVariable String email) {
        try {
            String message = passwordResetService.sendPasswordResetEmail(email);
            return ResponseEntity.ok(message); // 200 OK, e.g., "OTP sent"
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid email"); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send OTP"); // 500 Internal Server Error
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            String message = passwordResetService.resetPassword(passwordResetDTO);
            return ResponseEntity.ok(message); // 200 OK, e.g., "Password reset successful"
            //System.out.println();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid OTP or password"); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Password reset failed"); // 500 Internal Server Error
        }
    }
}
