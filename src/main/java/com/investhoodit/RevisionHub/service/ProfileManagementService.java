package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PasswordChangeDTO;
import com.investhoodit.RevisionHub.dto.ProfileUpdateRequest;
import com.investhoodit.RevisionHub.dto.UserResponse;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class ProfileManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;

    public ProfileManagementService(UserRepository userRepository, PasswordEncoderService passwordEncoderService) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
    }

    public ResponseEntity<ApiResponse<User>> updateProfile(ProfileUpdateRequest profileRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (profileRequest == null || (profileRequest.getFirstName() == null && profileRequest.getLastName() == null && profileRequest.getPhoneNumber() == null)) {
            ApiResponse<User> response = new ApiResponse<>(
                    "At least one field (firstName, lastName, phoneNumber) must be provided",
                    false,
                    null
            );

            return ResponseEntity.badRequest()
                    .body(response);
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

        ApiResponse<User> response = new ApiResponse<>(
                "Profile updated successfully",
                true,
                user
        );

        return ResponseEntity.status(200).body(response);
    }

    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(MultipartFile profilePicture) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse userResponse = new UserResponse();
        if (user.getProfilePicture() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
            userResponse.setProfilePicture("data:image/jpeg;base64," + base64Image);
        }


        if (profilePicture == null || profilePicture.isEmpty()) {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                    "Profile picture file is required",
                    false,
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }

        byte[] profilePictureBytes = profilePicture.getBytes();
        user.setProfilePicture(profilePictureBytes);

        userRepository.save(user);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                "Profile picture updated successfully",
                true,
                userResponse
        );

        return ResponseEntity.status(200).body(response);
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

    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(user.getId());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setEmail(user.getEmail());
            userResponse.setPhoneNumber(user.getPhoneNumber()); // Hardcoded or from user role
            if (user.getProfilePicture() != null) {
                String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
                userResponse.setProfilePicture("data:image/jpeg;base64," + base64Image);
            }

            ApiResponse<UserResponse> response = new ApiResponse<>(
                    "User details retrieved successfully",
                    true,
                    userResponse
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<UserResponse> response = new ApiResponse<>(
                    "Failed to retrieve user details: " + e.getMessage(),
                    false,
                    null
            );
            return ResponseEntity.status(500).body(response);
        }
    }

    public ResponseEntity<String> changePassword(PasswordChangeDTO passwordChangeDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            // Verify current password
            if (!passwordEncoderService.verifyPassword(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }

            // Update password
            String encodedNewPassword = passwordEncoderService.encodePassword(passwordChangeDTO.getNewPassword());
            user.setPassword(encodedNewPassword);
            userRepository.save(user);
            return ResponseEntity.ok(( "Password changed successfully"));
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body( "Current password is incorrect");
        } catch (Exception e) {

            return ResponseEntity.status(500).body("Internal server error");
        }

    }

    public ResponseEntity<String> deleteAccount(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            userRepository.delete(user);
            return ResponseEntity.ok("Account deleted successfully");
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}