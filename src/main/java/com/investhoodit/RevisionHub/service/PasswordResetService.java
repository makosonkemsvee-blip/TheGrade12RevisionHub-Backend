package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PasswordResetDTO;
import com.investhoodit.RevisionHub.model.PasswordResetToken;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.PasswordResetTokenRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                UserRepository userRepository,
                                PasswordEncoderService passwordEncoderService,
                                JavaMailSender mailSender) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
        this.mailSender = mailSender;
    }

    public String sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String otp = generateOTP();
        PasswordResetToken resetToken = new PasswordResetToken(otp, LocalDateTime.now().plusMinutes(10), user, email);
        logger.info("Generated OTP: {} for email: {}", otp, email);
        tokenRepository.save(resetToken);
        logger.info("Stored OTP for email: {}", email);

        try {
            sendOTPEmail(email, user.getFirstName(), user.getLastName(), otp);
        } catch (MessagingException e) {
            logger.error("Failed to send password reset email: {}", e.getMessage());
            throw new RuntimeException("Failed to send password reset email");
        }

        return "OTP sent successfully via email";
    }

    private void sendOTPEmail(String email, String firstName, String lastName, String otp) throws MessagingException {
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String resetUrl = "https://revisionhub.com/reset-password?email=" + encodedEmail + "&otp=" + otp;
        String subject = "Password Reset OTP - RevisionHub";
        String htmlBody = String.format(
                "<!DOCTYPE html>" +
                        "<html lang='en'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<title>RevisionHub Password Reset OTP</title>" +
                        "</head>" +
                        "<body style='margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,Helvetica,sans-serif;'>" +
                        "<table align='center' border='0' cellpadding='0' cellspacing='0' width='100%%' style='max-width:600px;background-color:#ffffff;margin:20px auto;'>" +
                        "<tr>" +
                        "<td style='background-color:#1f7a6e;padding:20px;text-align:center;'>" +
                        "<img src='https://via.placeholder.com/150x60?text=RevisionHub+Logo' alt='RevisionHub Logo' style='height:60px;display:block;margin:0 auto;'>" +
                        "</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td style='padding:30px;color:#333333;'>" +
                        "<h1 style='font-size:24px;margin:0 0 20px;color:#1f7a6e;text-align:center;'>Password Reset Request, %s %s</h1>" +
                        "<p style='font-size:16px;line-height:1.6;margin:0 0 20px;text-align:center;'>We received a request to reset your password for RevisionHub. Please use the following One-Time Password (OTP) to reset your password:</p>" +
                        "<div style='font-size:28px;font-weight:bold;color:#b91c1c;text-align:center;padding:15px;background-color:#f4f4f4;margin:20px 0;'>%s</div>" +
                        "<p style='font-size:16px;line-height:1.6;margin:0 0 20px;text-align:center;'>This OTP is valid for 10 minutes. Click the button below to reset your password.</p>" +
                        "<div style='text-align:center;'>" +
                        "<a href='%s' style='display:inline-block;padding:12px 24px;background-color:#1f7a6e;color:#ffffff;text-decoration:none;font-size:16px;'>Reset Your Password</a>" +
                        "</div>" +
                        "<p style='font-size:16px;line-height:1.6;margin:0 0 20px;text-align:center;'>If you did not initiate this request, please ignore this email or contact our support team.</p>" +
                        "</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td style='background-color:#f4f4f4;padding:20px;text-align:center;font-size:14px;color:#666666;'>" +
                        "<p style='margin:0 0 10px;'>© 2025 RevisionHub. All rights reserved.</p>" +
                        "<p style='margin:0;'>Need help? Contact us at <a href='mailto:support@revisionhub.com' style='color:#1f7a6e;text-decoration:none;'>support@revisionhub.com</a></p>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</body>" +
                        "</html>",
                firstName, lastName, otp, resetUrl
        );
        String textBody = String.format(
                "Password Reset Request, %s %s!\n\n" +
                        "We received a request to reset your password for RevisionHub. Please use the following One-Time Password (OTP) to reset your password: %s\n\n" +
                        "This OTP is valid for 10 minutes. Visit %s to reset your password.\n\n" +
                        "If you did not initiate this request, please ignore this email or contact support@revisionhub.com.",
                firstName, lastName, otp, resetUrl
        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(textBody, htmlBody);
        mailSender.send(message);
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