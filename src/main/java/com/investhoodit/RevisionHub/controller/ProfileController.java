package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.ProfileUpdateRequest;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.ProfileManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileManagementService profileManagementService;

    public ProfileController(ProfileManagementService profileManagementService) {
        this.profileManagementService = profileManagementService;
    }

    @PutMapping(path = "/update-profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest profileRequest) {
        return profileManagementService.updateProfile(profileRequest);
    }

    @PutMapping(path = "/update-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateProfilePicture(
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) throws IOException {
        return profileManagementService.updateProfilePicture(profilePicture);
    }

    @GetMapping("/picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        return profileManagementService.getProfilePicture(userId);
    }
}