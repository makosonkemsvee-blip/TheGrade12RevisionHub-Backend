package com.investhoodit.RevisionHub.model;

public class CoursePerformance {
    private String name;
    private double progress;

    public CoursePerformance(String name, double progress) {
        this.name = name;
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public double getProgress() {
        return progress;
    }
}