package com.investhoodit.RevisionHub.dto;

import lombok.Data;

@Data
public class DigitalizedQPRequest {
    private String paperTitle;
    private String subject;
    private Integer year;
    private Integer score;
}
