package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "settings")
@Data
public class Settings {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "email_notifications")
    private boolean emailNotifications = true;

    @Column(name = "push_notifications")
    private boolean pushNotifications = false;

    @Column(name = "chat_notifications")
    private boolean chatNotifications = true;

    @Column(name = "mention_notifications")
    private boolean mentionNotifications = true;

    @Column(name = "notification_sound")
    private boolean notificationSound = true;

    @Column(name = "theme")
    private String theme = "light";

    @Column(name = "font_size")
    private String fontSize = "medium";

    @Column(name = "language")
    private String language = "en";

    @Column(name = "profile_visibility")
    private String profileVisibility = "public";

    @Column(name = "data_sharing")
    private boolean dataSharing = false;

    @OneToOne
    @MapsId
    @JoinColumn(name = "username")
    private User user;
}
