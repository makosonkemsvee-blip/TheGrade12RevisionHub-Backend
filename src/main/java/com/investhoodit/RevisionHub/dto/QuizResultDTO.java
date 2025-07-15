package com.investhoodit.RevisionHub.dto;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class QuizResultDTO {
    private Long quizId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
}
