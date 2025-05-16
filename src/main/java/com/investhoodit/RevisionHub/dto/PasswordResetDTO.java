package com.investhoodit.RevisionHub.dto;

public class PasswordResetDTO {
    private String otp;
    private String newPassword;

    public PasswordResetDTO(String otp, String newPassword) {
        this.otp = otp;
        this.newPassword = newPassword;
    }

    public PasswordResetDTO() {
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
