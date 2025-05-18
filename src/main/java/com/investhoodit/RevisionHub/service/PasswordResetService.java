package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PasswordResetDTO;
import com.investhoodit.RevisionHub.model.PasswordResetToken;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.PasswordResetTokenRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                EmailService emailService, UserRepository userRepository,
                                PasswordEncoderService passwordEncoderService) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    public String sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user != null) {
            String otp = generateOTP();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(otp);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
            resetToken.setUser(user);
            tokenRepository.save(resetToken);

            String subject = "Password Reset OTP";
            String body = "Your OTP for password reset is: " + otp;
            emailService.sendEmail(email, subject, body);

            return "OTP sent successful via email";
        }else{
            throw new RuntimeException("User not found");
        }
    }

    private String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public boolean reset(String otp, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(otp);
        if (resetToken != null && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            User user = resetToken.getUser();
            user.setPassword(passwordEncoderService.encodePassword(newPassword));
            userRepository.save(user);
            tokenRepository.delete(resetToken);
            return true;
        }
        return false;
    }

    public String resetPassword(PasswordResetDTO passwordResetDTO) {
        String error = validateResetPasswordRequest(passwordResetDTO.getOtp(), passwordResetDTO.getNewPassword());
        if (error != null) {
            return error;
        }

        boolean success = reset(passwordResetDTO.getOtp(), passwordResetDTO.getNewPassword());
        if (success) {
            return "Password reset successful.";
        } else {
            return "Failed to reset password. Please check the OTP and try again. c";
        }
    }

    private String validateResetPasswordRequest(String otp, String password) {
        if (otp == null || otp.isEmpty()) {
            return "OTP must be provided.";
        }
        if (password == null || password.isEmpty()) {
            return "New password must be provided.";
        }
        return null;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
