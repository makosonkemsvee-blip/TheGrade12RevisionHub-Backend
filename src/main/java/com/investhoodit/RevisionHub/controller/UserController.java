package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.PasswordChangeDTO;
import com.investhoodit.RevisionHub.dto.UserDTO;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import com.investhoodit.RevisionHub.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserRepository userRepository;

    @Autowired
    private UserService userService;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching user for: {}", userDetails);
        if (userDetails == null) {
            log.warn("No UserDetails provided, returning 401");
            return ResponseEntity.status(401).body(null);
        }
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userDetails.getUsername());
                    return new EntityNotFoundException("User not found with email: " + userDetails.getUsername());
                });
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getTwoFactorEnabled()
        );
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setIdNumber(user.getIdNumber());
        userDTO.setProfilePicture(user.getProfilePicture() != null ? Base64.getEncoder().encodeToString(user.getProfilePicture()) : null);
        log.info("Returning userDTO: {}", userDTO);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@Valid @RequestBody UserUpdateDTO updateDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

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
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getCreatedAt(),
                user.getTwoFactorEnabled()
        );
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setIdNumber(user.getIdNumber());
        userDTO.setProfilePicture(user.getProfilePicture() != null ? Base64.getEncoder().encodeToString(user.getProfilePicture()) : null);
        return ResponseEntity.ok(userDTO);
    }

    public record UserUpdateDTO(
            @jakarta.validation.constraints.NotBlank String firstName,
            String lastName,
            @jakarta.validation.constraints.Email String email,
            String profilePicture
    ) {}

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PasswordChangeDTO passwordChangeDTO) {
        log.info("Attempting password change for user: {}", userDetails != null ? userDetails.getUsername() : "null");
        if (userDetails == null) {
            log.warn("No UserDetails provided, returning 401");
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        try {
            userService.changePassword(userDetails, passwordChangeDTO);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during password change: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/save-profile")
    public ResponseEntity<Map<String, String>> saveProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> profileData) {
        log.info("Attempting to save profile for user: {}", userDetails != null ? userDetails.getUsername() : "null");
        if (userDetails == null) {
            log.warn("No UserDetails provided, returning 401");
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        try {
            userService.saveProfile(userDetails.getUsername(), profileData);
            return ResponseEntity.ok(Map.of("message", "Profile saved successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Profile save failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during profile save: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

}