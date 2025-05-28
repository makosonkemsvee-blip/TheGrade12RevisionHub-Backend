/*
package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/users")
public class usersNotificationController {
    private final UserRepository userRepository;

    public usersNotificationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // Assumes userId is the username in JWT
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        UserDTO dto = new UserDTO(
                user.getId().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getProfilePicture() != null ? Base64.getEncoder().encodeToString(user.getProfilePicture()) : null

        );
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public record UserDTO(String id, String firstName, String lastName, String title, String profilePicture) {}
}*/
