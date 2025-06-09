package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "digitized_question_papers")
@Data
public class DigitizedQuestionPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Lob
    @Column(name = "file_data")
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "subject_name", referencedColumnName = "subject_name")
    private Subject subject;

    @Column(name = "is_interactive")
    private boolean isInteractive;
}