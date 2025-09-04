package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
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

}