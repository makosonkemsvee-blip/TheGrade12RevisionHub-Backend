package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;
    private String subject;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
