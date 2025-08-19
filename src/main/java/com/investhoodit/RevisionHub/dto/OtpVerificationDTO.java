package com.investhoodit.RevisionHub.dto;

import lombok.Data;

@Data
public class OtpVerificationDTO {
    private String email;
    private String otp;
}