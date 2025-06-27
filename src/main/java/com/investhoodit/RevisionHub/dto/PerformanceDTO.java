package com.investhoodit.RevisionHub.dto;

import java.time.LocalDate;

public record PerformanceDTO(
        Long id,
        Long userId,
        String subjectName,
        String activityType,
        String activityName,
        LocalDate date,
        Integer score,
        Integer maxScore,
        Integer timeSpent,
        String difficulty,
        String status,
        String comments
) {
    public PerformanceDTO(Long id, Long userId, String subjectName, String activityType, String activityName, LocalDate date, Integer score, Integer maxScore, Integer timeSpent, String difficulty, String status, String comments) {
        this.id = id;
        this.userId = userId;
        this.subjectName = subjectName;
        this.activityType = activityType;
        this.activityName = activityName;
        this.date = date;
        this.score = score;
        this.maxScore = maxScore;
        this.timeSpent = timeSpent;
        this.difficulty = difficulty;
        this.status = status;
        this.comments = comments;
    }

    @Override
    public Long id() { return id; }
    @Override
    public Long userId() { return userId; }
    @Override
    public String subjectName() { return subjectName; }
    @Override
    public String activityType() { return activityType; }
    @Override
    public String activityName() { return activityName; }
    @Override
    public LocalDate date() { return date; }
    @Override
    public Integer score() { return score; }
    @Override
    public Integer maxScore() { return maxScore; }
    @Override
    public Integer timeSpent() { return timeSpent; }
    @Override
    public String difficulty() { return difficulty; }
    @Override
    public String status() { return status; }
    @Override
    public String comments() { return comments; }
}