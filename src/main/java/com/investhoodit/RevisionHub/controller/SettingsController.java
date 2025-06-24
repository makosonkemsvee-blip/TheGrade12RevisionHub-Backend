package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.SettingsDTO;
import com.investhoodit.RevisionHub.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

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
}