package com.investhoodit.RevisionHub.dto;

public class ScoreResponseDTO {
    private Double score;
    private String errorMessage;

    public ScoreResponseDTO(Double score) {
        this.score = score;
        this.errorMessage = null;
    }

    public ScoreResponseDTO(String errorMessage) {
        this.score = null;
        this.errorMessage = errorMessage;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}