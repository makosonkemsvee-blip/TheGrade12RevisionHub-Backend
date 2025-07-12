package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionId;
    private String content;
    private String type; // NUMERICAL, ALGEBRAIC, MULTIPLE_CHOICE, PROOF
    private int marks;
    private String topic;
    private String difficulty;
    private String correctAnswer;
    private String solution;
    private String commonMistakes;

    public Question(String commonMistakes, String solution, String correctAnswer, String difficulty, String topic, int marks, String type, String content, String questionId) {
        this.commonMistakes = commonMistakes;
        this.solution = solution;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
        this.topic = topic;
        this.marks = marks;
        this.type = type;
        this.content = content;
        this.questionId = questionId;
    }

    public Question(String questionText, List<String> options, String correctAnswer, Quiz quiz) {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public void setCommonMistakes(String commonMistakes) {
        this.commonMistakes = commonMistakes;
    }

    public String getQuestionText() {
        return null;
    }
}
