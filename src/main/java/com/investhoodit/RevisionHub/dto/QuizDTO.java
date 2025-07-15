package com.investhoodit.RevisionHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class QuizDTO {

    @NotBlank(message = "Quiz Id is required")
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Subject ID is required")
    private String subjectId;

    @NotEmpty(message = "Questions list cannot be empty")
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        @NotBlank(message = "Question Id is required")
        private Long questionId;

        @NotBlank(message = "Question text is required")
        private String questionText;

        @NotEmpty(message = "Options list cannot be empty")
        private List<String> options;

        @NotBlank(message = "Correct answer is required")
        private String correctAnswer;

    }
}