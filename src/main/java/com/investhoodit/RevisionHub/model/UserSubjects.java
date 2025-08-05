package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

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
}
