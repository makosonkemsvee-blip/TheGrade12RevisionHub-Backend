package com.investhoodit.RevisionHub.controller;


import com.investhoodit.RevisionHub.dto.SettingsDTO;
import com.investhoodit.RevisionHub.service.SettingsService;
import com.investhoodit.RevisionHub.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/user/users")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private UserService userService;

    @GetMapping("/settings")
    public ResponseEntity<SettingsDTO> getSettings(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        SettingsDTO settings = settingsService.getSettings(userDetails.getUsername());
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/settings")
    public ResponseEntity<SettingsDTO> updateSettings(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SettingsDTO settingsDTO) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        SettingsDTO updatedSettings = settingsService.updateSettings(userDetails.getUsername(), settingsDTO);
        return ResponseEntity.ok(updatedSettings);
    }

    @PostMapping("/deleteUser")
    @ResponseBody
    public ResponseEntity<String> deleteAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails,
                                                          HttpServletRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No logged in user");
        }

        boolean deleted = userService.removeUser(userDetails.getUsername());
        if (deleted) {
            // Invalidate session to log out the user (if session exists)
            if (request.getSession(false) != null) {
                request.getSession(false).invalidate();
            }
            return ResponseEntity.ok("Account deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}