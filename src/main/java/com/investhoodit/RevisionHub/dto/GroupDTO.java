package com.investhoodit.RevisionHub.dto;

import java.time.LocalDateTime;

public class GroupDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private Long creatorId;

    public GroupDTO(Long id, String name, LocalDateTime createdAt, Long creatorId) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.creatorId = creatorId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
}