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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileRequest == null || (profileRequest.getFirstName() == null && profileRequest.getLastName() == null && profileRequest.getPhoneNumber() == null)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("At least one field (firstName, lastName, phoneNumber) must be provided", false, null));
        }

        if (profileRequest.getFirstName() != null) {
            user.setFirstName(profileRequest.getFirstName());
        }
        if (profileRequest.getLastName() != null) {
            user.setLastName(profileRequest.getLastName());
        }
        if (profileRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(profileRequest.getPhoneNumber());
        }

        userRepository.save(user);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Profile updated successfully", true, user));
    }

    public ResponseEntity<ApiResponse> updateProfilePicture(MultipartFile profilePicture) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profilePicture == null || profilePicture.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Profile picture file is required", false, null));
        }

        byte[] profilePictureBytes = profilePicture.getBytes();
        user.setProfilePicture(profilePictureBytes);

        userRepository.save(user);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Profile picture updated successfully", true, user));
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