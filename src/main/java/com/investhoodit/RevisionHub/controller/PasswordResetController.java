package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.PasswordResetDTO;
import com.investhoodit.RevisionHub.service.PasswordResetService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/send-otp/{email}")
    public String requestPasswordReset(@PathVariable String email) {
        return passwordResetService.sendPasswordResetEmail(email);
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        return passwordResetService.resetPassword(passwordResetDTO);
    }
}
