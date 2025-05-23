package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "question_papers")
@Data
public class QuestionPaper implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "subject_name", referencedColumnName = "subject_name")
    private Subject subject;

}

