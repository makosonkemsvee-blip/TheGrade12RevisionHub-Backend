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

    @Column(name = "activity_id", nullable = false)
    private Long activityId; // lana kutoba i ID yeQuiz or yeDigitized QP

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double score;

    public PerformanceMetric() {
    }
}
