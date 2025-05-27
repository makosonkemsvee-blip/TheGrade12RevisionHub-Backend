package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public UserLoginService(UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Transactional
    public void authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Update firstLogin flag after successful login

        if (user.isFirstLogin()) {
            //logger.info("First login detected for user ID: {}, sending welcome notification", user.getId());
            String welcomeMessage = "Welcome to the System, we are pleased to have you onboard, " + user.getFirstName() + " " + user.getLastName() + "!";
            notificationService.createNotification(user.getId().toString(), welcomeMessage, "WELCOME");
           // logger.info("Welcome notification sent for user ID: {}", user.getId());

            user.setFirstLogin(false);
            userRepository.save(user);
            //logger.info("Updated firstLogin to false for user ID: {}", user.getId());
        }
    }
}
