package com.investhoodit.RevisionHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class QuizDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Subject ID is required")
    private String subjectId;

    @NotEmpty(message = "Questions list cannot be empty")
    private List<QuestionDTO> questions;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public List<QuestionDTO> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDTO> questions) { this.questions = questions; }

    public static class QuestionDTO {
        @NotBlank(message = "Question text is required")
        private String questionText;

        @NotEmpty(message = "Options list cannot be empty")
        private List<String> options;

        @NotBlank(message = "Correct answer is required")
        private String correctAnswer;

        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    }
}