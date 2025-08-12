package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class UserSignupService {
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final EmailService emailService;
    private final JavaMailSender mailSender;

    public UserSignupService(UserRepository userRepository, PasswordEncoderService passwordEncoderService, EmailService emailService, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
        this.emailService = emailService;
        this.mailSender = mailSender;
    }

    public User signUp(UserDTO userDTO) throws Exception {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new Exception("User already exists");
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setIdNumber(userDTO.getIdNumber());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoderService.encodePassword(userDTO.getPassword()));
        user.setPhoneNumber(userDTO.getPhoneNumber());
        String role = userDTO.getRole() != null && userDTO.getRole().equals("ADMIN") ? "ADMIN" : "USER";
        user.setRole(role);
        user.setFirstLogin(true);
        user.setIsVerified(false);

        String otp = generateOTP();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        User savedUser = userRepository.save(user);

        sendOTPEmail(userDTO.getEmail(), userDTO.getFirstName(), userDTO.getLastName(), otp);

        return savedUser;
    }

    private void sendOTPEmail(String email, String firstName, String lastName, String otp) throws MessagingException {
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String verifyUrl = "https://revisionhub.com/verify-otp?email=" + encodedEmail;
        String subject = "Verify Your Email - RevisionHub OTP";
        String htmlBody = String.format(
                "<!DOCTYPE html>" +
                        "<html lang='en'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<title>RevisionHub OTP</title>" +
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
                        "<h1 style='font-size:24px;margin:0 0 20px;color:#1f7a6e;text-align:center;'>Welcome, %s %s!</h1>" +
                        "<p style='font-size:16px;line-height:1.6;margin:0 0 20px;text-align:center;'>Thank you for joining RevisionHub. Please use the following One-Time Password (OTP) to verify your email address:</p>" +
                        "<div style='font-size:28px;font-weight:bold;color:#b91c1c;text-align:center;padding:15px;background-color:#f4f4f4;margin:20px 0;'>%s</div>" +
                        "<p style='font-size:16px;line-height:1.6;margin:0 0 20px;text-align:center;'>This OTP is valid for 10 minutes. Enter it on the verification page to activate your account.</p>" +
                        "<div style='text-align:center;'>" +
                        "<a href='%s' style='display:inline-block;padding:12px 24px;background-color:#1f7a6e;color:#ffffff;text-decoration:none;font-size:16px;'>Verify Your Email</a>" +
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
                firstName, lastName, otp, verifyUrl
        );
        String textBody = String.format(
                "Welcome, %s %s!\n\n" +
                        "Thank you for joining RevisionHub. Please use the following One-Time Password (OTP) to verify your email address: %s\n\n" +
                        "This OTP is valid for 10 minutes. Enter it at %s to activate your account.\n\n" +
                        "If you did not initiate this request, please ignore this email or contact support@revisionhub.com.",
                firstName, lastName, otp, verifyUrl
        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(textBody, htmlBody);
        mailSender.send(message);
    }

    public boolean verifyOTP(String email, String otp) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (user.getOtpCode() == null || user.getOtpExpiry() == null) {
                        return false;
                    }
                    boolean isValid = user.getOtpCode().equals(otp) && LocalDateTime.now().isBefore(user.getOtpExpiry());
                    if (isValid) {
                        user.setIsVerified(true);
                        user.setOtpCode(null);
                        user.setOtpExpiry(null);
                        userRepository.save(user);

                        String subject = "Welcome to RevisionHub!";
                        String htmlBody = String.format(
                                "<!DOCTYPE html>" +
                                        "<html lang='en'>" +
                                        "<head>" +
                                        "<meta charset='UTF-8'>" +
                                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                                        "<title>Welcome to RevisionHub</title>" +
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
                                        "<h1 style='font-size:24px;margin:0 0 20px;color:#1f7a6e;text-align:center;'>Welcome, %s %s!</h1>" +
                                        "<p style='font-size:16px;line-height:1.6;margin:0 0 20px;text-align:center;'>Congratulations! Your email has been successfully verified, and you're now a part of RevisionHub.</p>" +
                                        "<p style='font-size:16px;line-height:1.6;margin:0 0 20px;text-align:center;'>Start exploring our interactive quizzes, personalized study plans, and progress tracking tools to ace your Grade 12 studies.</p>" +
                                        "<div style='text-align:center;'>" +
                                        "<a href='https://revisionhub.com/login' style='display:inline-block;padding:12px 24px;background-color:#1f7a6e;color:#ffffff;text-decoration:none;font-size:16px;'>Log In to Get Started</a>" +
                                        "</div>" +
                                        "<p style='font-size:16px;line-height:1.6;margin:0 0 20px;text-align:center;'>We're excited to support your learning journey. If you need assistance, our support team is here to help.</p>" +
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
                                user.getFirstName(), user.getLastName()
                        );
                        String textBody = String.format(
                                "Welcome, %s %s!\n\n" +
                                        "Congratulations! Your email has been successfully verified, and you're now a part of RevisionHub.\n\n" +
                                        "Start exploring our interactive quizzes, personalized study plans, and progress tracking tools at https://revisionhub.com/login.\n\n" +
                                        "We're excited to support your learning journey. If you need assistance, contact us at support@revisionhub.com.",
                                user.getFirstName(), user.getLastName()
                        );

                        try {
                            MimeMessage message = mailSender.createMimeMessage();
                            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                            helper.setTo(user.getEmail());
                            helper.setSubject(subject);
                            helper.setText(textBody, htmlBody);
                            mailSender.send(message);
                        } catch (MessagingException e) {
                            System.err.println("Failed to send welcome email: " + e.getMessage());
                        }
                    }
                    return isValid;
                })
                .orElse(false);
    }

    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    public String resendOTP(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));

        if (user.getIsVerified()) {
            throw new Exception("Email already verified");
        }

        // Check cooldown (2 minutes since last OTP)
        if (user.getOtpExpiry() != null && LocalDateTime.now().isBefore(user.getOtpExpiry().minusMinutes(8))) {
            return "An OTP has been recently sent. Please check your email or wait a few minutes to request a new one.";
        }

        // Generate and send new OTP
        String otp = generateOTP();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        sendOTPEmail(email, user.getFirstName(), user.getLastName(), otp);
        return "OTP sent successfully. Check your email.";
    }
}