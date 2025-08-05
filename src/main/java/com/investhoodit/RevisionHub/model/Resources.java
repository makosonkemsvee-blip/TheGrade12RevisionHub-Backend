package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
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
}