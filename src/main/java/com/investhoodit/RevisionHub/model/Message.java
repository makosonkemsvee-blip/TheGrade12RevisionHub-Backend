package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long recipientId;
    private String content;
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    private Long groupId;

    @Transient
    private String senderName;
    @Transient
    private String messageSnippet;
    @Transient
    private String groupName;

    public Message() {
        this.createdAt = Instant.now(); // UTC time
    }

}