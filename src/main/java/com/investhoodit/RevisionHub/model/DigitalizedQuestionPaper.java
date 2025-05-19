package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Entity
public class DigitalizedQuestionPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long PaperId;
    private String PaperTitle;
    private String subject;
    private String percentage;
    private LocalDateTime submittedDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "submitter")
    private User submitter;
}
