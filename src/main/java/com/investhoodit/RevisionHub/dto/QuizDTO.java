package com.investhoodit.RevisionHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuizDTO {

    @NotNull(message = "Quiz Id is required")
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Subject ID is required")
    private String subject;

    @NotEmpty(message = "Questions list cannot be empty")
    private List<QuestionDTO> questions;

    @Data
    public static class QuestionDTO {
        @NotNull(message = "Question Id is required")
        private Long questionId;

        @NotBlank(message = "Question text is required")
        private String questionText;

        @NotEmpty(message = "Options list cannot be empty")
        private List<String> options;

        @NotBlank(message = "Correct answer is required")
        private String correctAnswer;
    }

    // New DTO for quiz creation
    @Data
    public static class CreateQuizDTO {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Description is required")
        private String description;

        @NotBlank(message = "Subject ID is required")
        private String subject;

        @NotEmpty(message = "Questions list cannot be empty")
        private List<CreateQuestionDTO> questions;

        @Data
        public static class CreateQuestionDTO {
            @NotBlank(message = "Question text is required")
            private String questionText;

            @NotEmpty(message = "Options list cannot be empty")
            private List<String> options;

            @NotBlank(message = "Correct answer is required")
            private String correctAnswer;
        }
    }
}