package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.stereotype.Service;

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
        try {
            if (userRepository.findByEmail(userDTO.getEmail()).isEmpty()) {
                User user = new User();
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setIdNumber(userDTO.getIdNumber());
                user.setEmail(userDTO.getEmail());
                user.setPassword(userDTO.getPassword());
                user.setPhoneNumber(userDTO.getPhoneNumber());
                // Set role to STUDENT if specified in DTO, otherwise default to USER
                String role = userDTO.getRole() != null && userDTO.getRole().equals("STUDENT") ? "STUDENT" : "USER";
                user.setRole(role);
                user.setFirstLogin(true);
                user.setPassword(passwordEncoderService.encodePassword(user.getPassword()));
                User savedUser = userRepository.save(user);

                // Send email notification only for STUDENT role
                if ("STUDENT".equals(role)) {
                    String subject = "Welcome to RevisionHub!";
                    String body = String.format("Dear %s %s,\n\nCongratulations! You have successfully registered as a student on RevisionHub. " +
                                    "We're excited to have you on board. You can now start exploring the platform and accessing your study resources.\n\n" +
                                    "Best regards,\nThe RevisionHub Team",
                            userDTO.getFirstName(), userDTO.getLastName());
                    emailService.sendEmail(userDTO.getEmail(), subject, body);
                }

                return savedUser;
            }
        } catch (Exception e) {
            throw new Exception("User already exists");
        }
        return null;
    }
}