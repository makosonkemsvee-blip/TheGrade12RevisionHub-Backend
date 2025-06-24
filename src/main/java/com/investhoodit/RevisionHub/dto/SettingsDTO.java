package com.investhoodit.RevisionHub.dto;

import lombok.Data;

@Data
public class SettingsDTO {
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean chatNotifications;
    private boolean mentionNotifications;
    private boolean notificationSound;
    private String theme;
    private String fontSize;
    private String language;
    private String profileVisibility;
    private boolean dataSharing;
}