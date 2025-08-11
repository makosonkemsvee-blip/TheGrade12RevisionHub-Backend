package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.LoginRequest;
import com.investhoodit.RevisionHub.dto.LoginResponse;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final UserService userService;

    public UserLoginService(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationService notificationService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    public LoginResponse authenticateAndGenerateToken(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Record login for attendance tracking
        userService.recordLogin(loginRequest.getEmail());


        // Update firstLogin flag after successful login
        if (user.isFirstLogin()) {
            //logger.info("First login detected for user ID: {}, sending welcome notification", user.getId());
            String welcomeMessage = "Welcome to the 'Grade 12 Revision Hub', We are pleased to have you onboard, " + user.getFirstName() + " " + user.getLastName() + " 📚📚📚!";
            notificationService.createNotification(user.getId(), welcomeMessage, "WELCOME");
            // logger.info("Welcome notification sent for user ID: {}", user.getId());

            user.setFirstLogin(false);
            userRepository.save(user);
        }
        String token = jwtUtil.generateJwtToken(user.getEmail());
        return new LoginResponse(token, "Login successful", user.getRole());
    }

}