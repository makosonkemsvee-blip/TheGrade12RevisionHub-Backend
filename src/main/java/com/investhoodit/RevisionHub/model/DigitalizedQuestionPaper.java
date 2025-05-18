package com.investhoodit.RevisionHub.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DigitalizedQuestionPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long PaperId;
    private String PaperTitle;
    private String subject;
    private String percentage;
    private LocalDateTime submittedDate = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "submitter")
    private User submitter;

    public DigitalizedQuestionPaper() {}
    public Long getPaperId() {
        return PaperId;
    }
    public void setPaperId(Long PaperId) {
        this.PaperId = PaperId;
    }
    public String getPaperTitle() {
        return PaperTitle;
    }
    public void setPaperTitle(String PaperTitle) {
        this.PaperTitle = PaperTitle;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getPercentage() {
        return percentage;
    }
    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }
    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }
    public User getSubmitter() {
        return submitter;
    }
    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }
}
