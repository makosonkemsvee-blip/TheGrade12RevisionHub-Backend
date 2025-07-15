package com.investhoodit.RevisionHub.dto;

import lombok.Data;

import java.util.List;
@Data
public class QuizSubmissionDTO {
    private List<AnswerDTO> answers;

    @Data
    public static class AnswerDTO {
        private Long questionId;
        private String selectedAnswer;

    }
}
