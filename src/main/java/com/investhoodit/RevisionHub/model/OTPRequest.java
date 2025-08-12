package com.investhoodit.RevisionHub.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class OTPRequest {
    private String email;
        private String otp;

    public void setEmail(String email) {
            this.email = email;
        }

    public void setOtp(String otp) {
            this.otp = otp;
        }
}
