package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_paper_performance")
@Data
public class UserPaperPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private DigitizedQuestionPaper paper;

    private int score;
    private int maxScore;
    private LocalDateTime attemptDate;

    // Calculated fields
    private int attempts;
    private double averageScore;
    private int highestScore;
}
