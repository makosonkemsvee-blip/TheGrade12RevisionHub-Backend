package com.investhoodit.RevisionHub.dto;

public class SubjectMasteryDTO {
    private String subjectName;
    private Integer quizMarks;
    private Integer examMarks;

    // Default constructor
    public SubjectMasteryDTO() {}

    // Parameterized constructor
    public SubjectMasteryDTO(String subjectName, Integer quizMarks, Integer examMarks) {
        this.subjectName = subjectName;
        this.quizMarks = quizMarks;
        this.examMarks = examMarks;
    }

    // Getters and setters
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getQuizMarks() {
        return quizMarks;
    }

    public void setQuizMarks(Integer quizMarks) {
        this.quizMarks = quizMarks;
    }

    public Integer getExamMarks() {
        return examMarks;
    }

    public void setExamMarks(Integer examMarks) {
        this.examMarks = examMarks;
    }
}