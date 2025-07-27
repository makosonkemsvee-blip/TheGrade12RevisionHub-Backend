package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subject_mastery")
public class SubjectMastery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "subject_name", referencedColumnName = "subject_name")
    private Subject subject;

    @Column(name = "progress", nullable = false)
    private double progress = 0.0; // Default to 0.0, updated during migration

    // Getters, setters, constructors
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
}