package com.investhoodit.RevisionHub.dto;

import lombok.*;

@Data
public class PasswordResetDTO {
    private String otp;
    private String newPassword;
    private String email;
}
