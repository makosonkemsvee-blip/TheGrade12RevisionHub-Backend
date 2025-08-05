package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id") // Map to the numeric user_id column
    private Long userId; // Change to Long to match numeric user_id

    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;

    private String senderName;
    private String messageSnippet;
    private String groupName; // Group notification name

    public void setIsRead(boolean b) {
    }
}