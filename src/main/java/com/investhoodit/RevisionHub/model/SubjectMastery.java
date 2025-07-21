package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subject_mastery")
public class SubjectMastery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "quiz_marks")
    private Integer quizMarks;

    @Column(name = "exam_marks")
    private Integer examMarks;

    @Column(name = "progress", nullable = false)
    private double progress = 0.0; // Default to 0.0, updated during migration


    // Getters, setters, constructors
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public Integer getQuizMarks() { return quizMarks; }
    public void setQuizMarks(Integer quizMarks) { this.quizMarks = quizMarks; }
    public Integer getExamMarks() { return examMarks; }
    public void setExamMarks(Integer examMarks) { this.examMarks = examMarks; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
}