package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
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

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@Valid @RequestBody UserUpdateDTO updateDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        user.setFirstName(updateDTO.firstName());
        user.setLastName(updateDTO.lastName());
        user.setEmail(updateDTO.email());
        if (updateDTO.profilePicture() != null && !updateDTO.profilePicture().isBlank()) {
            try {
                String base64Image = updateDTO.profilePicture().replaceFirst("^data:image/[^;]+;base64,", "");
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                user.setProfilePicture(imageBytes);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid base64 image data", e);
            }
        } else {
            user.setProfilePicture(null);
        }

        user = userRepository.save(user);
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

    public record UserUpdateDTO(
            @jakarta.validation.constraints.NotBlank String firstName,
            String lastName,
            @jakarta.validation.constraints.Email String email,
            String profilePicture
    ) {}
}