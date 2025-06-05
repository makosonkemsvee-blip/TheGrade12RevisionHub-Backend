package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "quizzes")
@Data
public class Quiz implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "subject_name", referencedColumnName = "subject_name")
    private Subject subject;

    private LocalDate dueDate;
}