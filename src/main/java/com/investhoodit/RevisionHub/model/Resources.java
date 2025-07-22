package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "resources")
public class Resources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String url;


    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "subject_name", referencedColumnName = "subject_name")
    private Subject subject;

    @Column
    private String fileName;

    @Column
    private String fileType;

    @Column(nullable = false)
    private String resourceType;

    @ElementCollection
    @CollectionTable(name = "resource_tags", joinColumns = @JoinColumn(name = "resource_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    // Constructors
    public Resources() {}

    public Resources(Long id, String title, String url, String description, Subject subject, String fileName, String fileType, String resourceType, List<String> tags, LocalDateTime uploadedAt) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.description = description;
        this.subject = subject;
        this.fileName = fileName;
        this.fileType = fileType;
        this.resourceType = resourceType;
        this.tags = tags;
        this.uploadedAt = uploadedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}