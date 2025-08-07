package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.PasswordChangeDTO;
import com.investhoodit.RevisionHub.dto.ProfileUpdateRequest;
import com.investhoodit.RevisionHub.dto.UserResponse;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.ProfileManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/user/profile")
public class ProfileController {

    private final ProfileManagementService profileManagementService;


    public ProfileController(ProfileManagementService profileManagementService) {
        this.profileManagementService = profileManagementService;
    }

    @PutMapping(path = "/update-profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<User>> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest profileRequest) {
        return profileManagementService.updateProfile(profileRequest);
    }

    @PutMapping(path = "/update-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) throws IOException {
        return profileManagementService.updateProfilePicture(profilePicture);
    }

    @GetMapping("/picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        return profileManagementService.getProfilePicture(userId);
    }

    @GetMapping("/getUserDetails")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails(){
        return profileManagementService.getUserDetails();
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody PasswordChangeDTO passwordChangeDTO) {

        try {
            profileManagementService.changePassword(passwordChangeDTO);
            return ResponseEntity.ok(( "Password changed successfully"));
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body( "Current password is incorrect");
        } catch (Exception e) {

            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}