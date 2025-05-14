package com.investhoodit.RevisionHub.service;

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

    public void sendPasswordResetEmail(String email) {
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
        }
    }

    private String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public boolean resetPassword(String otp, String newPassword) {
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
}
