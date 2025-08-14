package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.SettingsDTO;
import com.investhoodit.RevisionHub.model.Settings;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;
    private UserService userService;

    public SettingsDTO getSettings(String username) {
        Settings settings = settingsRepository.findById(username)
                .orElseGet(() -> createDefaultSettings(username));
        return mapToDTO(settings);
    }

    public SettingsDTO updateSettings(String username, SettingsDTO settingsDTO) {
        Settings settings = settingsRepository.findById(username)
                .orElseGet(() -> createDefaultSettings(username));
        updateSettingsFromDTO(settings, settingsDTO);
        settings = settingsRepository.save(settings);
        return mapToDTO(settings);
    }

    private Settings createDefaultSettings(String username) {
        Settings settings = new Settings();
        settings.setUsername(username);
        User user = new User();
        user.setFirstName(username);
        settings.setUser(user);
        return settingsRepository.save(settings);
    }

    private SettingsDTO mapToDTO(Settings settings) {
        SettingsDTO dto = new SettingsDTO();
        dto.setEmailNotifications(settings.isEmailNotifications());
        dto.setPushNotifications(settings.isPushNotifications());
        dto.setChatNotifications(settings.isChatNotifications());
        dto.setMentionNotifications(settings.isMentionNotifications());
        dto.setNotificationSound(settings.isNotificationSound());
        dto.setTheme(settings.getTheme());
        dto.setFontSize(settings.getFontSize());
        dto.setLanguage(settings.getLanguage());
        dto.setProfileVisibility(settings.getProfileVisibility());
        dto.setDataSharing(settings.isDataSharing());
        return dto;
    }

    private void updateSettingsFromDTO(Settings settings, SettingsDTO dto) {
        settings.setEmailNotifications(dto.isEmailNotifications());
        settings.setPushNotifications(dto.isPushNotifications());
        settings.setChatNotifications(dto.isChatNotifications());
        settings.setMentionNotifications(dto.isMentionNotifications());
        settings.setNotificationSound(dto.isNotificationSound());
        settings.setTheme(dto.getTheme());
        settings.setFontSize(dto.getFontSize());
        settings.setLanguage(dto.getLanguage());
        settings.setProfileVisibility(dto.getProfileVisibility());
        settings.setDataSharing(dto.isDataSharing());
    }

    public void deleteUser(String username) {
        userService.removeUser(username);
    }
}