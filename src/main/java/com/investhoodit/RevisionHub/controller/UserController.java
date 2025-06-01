package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // Email from JWT via SecurityContext
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        UserDTO dto = new UserDTO(
                user.getId().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getProfilePicture() != null ? Base64.getEncoder().encodeToString(user.getProfilePicture()) : null
        );
        return ResponseEntity.ok(dto);
    }

    public record UserDTO(String id, String firstName, String lastName, String role, String profilePicture) {}
}