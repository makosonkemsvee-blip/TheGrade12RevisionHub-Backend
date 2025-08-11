package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UserSignupService {
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final EmailService emailService;

    public UserSignupService(UserRepository userRepository, PasswordEncoderService passwordEncoderService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
        this.emailService = emailService;
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
        user.setVerified(false);

        // Generate OTP
        String otp = generateOTP();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10)); // OTP valid for 10 minutes

        User savedUser = userRepository.save(user);

        // Send OTP email
        String subject = "Verify Your Email - RevisionHub OTP";
        String body = String.format("Dear %s %s,\n\nThank you for registering with RevisionHub. " +
                        "Please use the following OTP to verify your email address: %s\n\n" +
                        "This OTP is valid for 10 minutes.\n\nBest regards,\nThe RevisionHub Team",
                userDTO.getFirstName(), userDTO.getLastName(), otp);
        emailService.sendEmail(userDTO.getEmail(), subject, body);

        return savedUser;
    }

    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    public boolean verifyOTP(String email, String otp) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    if (user.getOtpCode() == null || user.getOtpExpiry() == null) {
                        return false;
                    }
                    boolean isValid = user.getOtpCode().equals(otp) && LocalDateTime.now().isBefore(user.getOtpExpiry());
                    if (isValid) {
                        user.setVerified(true);
                        user.setOtpCode(null);
                        user.setOtpExpiry(null);
                        userRepository.save(user);

                        // Send welcome email after successful verification
                        String subject = "Welcome to RevisionHub!";
                        String body = String.format("Dear %s %s,\n\nCongratulations! Your email has been successfully verified. " +
                                        "Welcome to RevisionHub! You can now log in and start exploring the platform to access your study resources.\n\n" +
                                        "Best regards,\nThe RevisionHub Team",
                                user.getFirstName(), user.getLastName());
                        emailService.sendEmail(user.getEmail(), subject, body);
                    }
                    return isValid;
                })
                .orElse(false);
    }
}