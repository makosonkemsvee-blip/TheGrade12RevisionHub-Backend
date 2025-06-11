package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_subjects")
@Data
public class UserSubjects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_name", nullable = false)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Date createdAt;

    public UserSubjects(Subject subject, User user) {
    }

    public UserSubjects() {

    }

    @Entity
    @Table(name = "quizzes")
    @Data
    public static class Quiz implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String title;

        @ManyToOne
        @JoinColumn(name = "subject_name", referencedColumnName = "subject_name")
        private Subject subject;

        private LocalDate dueDate;
    }
}
