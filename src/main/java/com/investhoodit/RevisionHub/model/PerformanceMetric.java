package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "performance_metrics")
@Data
public class PerformanceMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "subject_name", referencedColumnName = "subject_name", nullable = false)
    private Subject subject;

    @Column(name = "activity_type", nullable = false)
    private String activityType;

    @Column(name = "activity_name", nullable = false)
    private String activityName;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int score;

    @Column(name = "max_score", nullable = false)
    private int maxScore;

    @Column(name = "time_spent", nullable = false)
    private int timeSpent;

    private String difficulty;

    @Column(nullable = false)
    private String status;

    private String comments;

    public PerformanceMetric() {
    }
}
