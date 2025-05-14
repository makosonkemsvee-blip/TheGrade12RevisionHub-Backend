package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.service.ProfileManagementService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/profileManagement")
public class ProfileManagementController {
    private final ProfileManagementService profileManagementService;

    public ProfileManagementController(ProfileManagementService profileManagementService) {
        this.profileManagementService = profileManagementService;
    }

    @PutMapping("/updateFullName")
    public User updateFullName(@RequestBody User user, HttpSession session) {
        profileManagementService.updateUserFullName(user,session);
        return user;
    }
    @PutMapping("/updatePhoneNumber")
    public User updatePhoneNumber(@RequestBody User user, HttpSession session) {
        profileManagementService.updateUserPhoneNumber(user,session);
        return user;
    }
    @PutMapping("/updateProfilePicture")
    public void updateProfilePicture(@RequestBody MultipartFile multipartFile, HttpSession session) throws IOException {
        profileManagementService.updateUserProfilePicture(multipartFile,session);
    }
    @GetMapping("/profile-picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        return profileManagementService.getProfilePicture(userId);
    }
}
