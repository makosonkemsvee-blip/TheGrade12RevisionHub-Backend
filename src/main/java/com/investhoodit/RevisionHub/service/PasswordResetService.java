package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PasswordResetDTO;
import com.investhoodit.RevisionHub.model.PasswordResetToken;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.PasswordResetTokenRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

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
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String otp = generateOTP();
        PasswordResetToken resetToken = new PasswordResetToken(otp, LocalDateTime.now().plusMinutes(10), user, email);
        logger.info("Generated OTP: {} for email: {}", otp, email);
        tokenRepository.save(resetToken);
        logger.info("Stored OTP for email: {}", email);

        String subject = "Password Reset OTP";
        String body = "Your OTP for password reset is: " + otp;
        emailService.sendEmail(email, subject, body);

        return "OTP sent successfully via email";
    }

    private String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    public boolean verifyOtp(String email, String otp) {
        logger.info("Verifying OTP: {} for email: {}", otp, email);
        PasswordResetToken resetToken = tokenRepository.findByTokenAndEmail(otp, email);
        if (resetToken == null) {
            logger.warn("No OTP found for email: {} with OTP: {}", email, otp);
            return false;
        }
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            logger.warn("OTP expired for email: {}", email);
            tokenRepository.delete(resetToken);
            return false;
        }
        logger.info("OTP verified successfully for email: {}", email);
        return true;
    }

    public boolean reset(String otp, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(otp);
        if (resetToken != null && resetToken.getExpiryDate().isAfter(LocalDateTime.now())) {
            User user = resetToken.getUser();
            user.setPassword(passwordEncoderService.encodePassword(newPassword));
            userRepository.save(user);
            tokenRepository.delete(resetToken);
            logger.info("Password reset successful for user: {}", user.getEmail());
            return true;
        }
        logger.warn("Password reset failed: Invalid or expired OTP: {}", otp);
        return false;
    }

    public String resetPassword(PasswordResetDTO passwordResetDTO) {
        String error = validateResetPasswordRequest(passwordResetDTO.getOtp(), passwordResetDTO.getNewPassword());
        if (error != null) {
            logger.warn("Validation error: {}", error);
            throw new IllegalArgumentException(error);
        }

        boolean success = reset(passwordResetDTO.getOtp(), passwordResetDTO.getNewPassword());
        if (success) {
            return "Password reset successful.";
        } else {
            throw new IllegalArgumentException("Failed to reset password. Please check the OTP and try again.");
        }
    }

    private String validateResetPasswordRequest(String otp, String password) {
        if (otp == null || otp.isEmpty()) {
            return "OTP must be provided.";
        }
        if (password == null || password.isEmpty()) {
            return "New password must be provided.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        return null;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}