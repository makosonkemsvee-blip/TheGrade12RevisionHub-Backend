package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.ProfileUpdateRequest;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfileManagementService {

    private final UserRepository userRepository;

    public ProfileManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<ApiResponse> updateProfile(ProfileUpdateRequest profileRequest) {
        // Get the logged-in user's email from the JWT token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileRequest != null) {
            // Update fields if provided
            if (profileRequest.getFirstName() != null) {
                user.setFirstName(profileRequest.getFirstName());
            }
            if (profileRequest.getLastName() != null) {
                user.setLastName(profileRequest.getLastName());
            }
            if (profileRequest.getPhoneNumber() != null) {
                user.setPhoneNumber(profileRequest.getPhoneNumber());
            }
        }else {
            throw new RuntimeException("Everything already up-to-date");
        }

        // Save updated user
        userRepository.save(user);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Profile updated successfully", true, user));
    }

    public ResponseEntity<ApiResponse> updateProfilePicture(MultipartFile profilePicture) throws IOException {
        // Get the logged-in user's email from the JWT token
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (profilePicture != null && !profilePicture.isEmpty()) {
            byte[] profilePictureBytes = profilePicture.getBytes();
            user.setProfilePicture(profilePictureBytes);
        }
        userRepository.save(user);

        return ResponseEntity.ok()
                .body(new ApiResponse("Profile updated successfully", true, user));
    }

    public ResponseEntity<byte[]> getProfilePicture(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getProfilePicture() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] image = user.getProfilePicture();
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }
}