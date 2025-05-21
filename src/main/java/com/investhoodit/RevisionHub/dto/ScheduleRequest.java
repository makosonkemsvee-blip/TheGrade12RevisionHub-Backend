package com.investhoodit.RevisionHub.dto;

import lombok.Data;

@Data
public class ScheduleRequest {
    private String subject;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
}
