package com.investhoodit.RevisionHub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class ScheduleRequest {
    private Long scheduleId; // Added for update/delete
    @NotBlank(message = "Subject is required")
    private String subject;
    @NotBlank(message = "Day of week is required")
    private String dayOfWeek; // e.g., "M", "T"
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    @NotNull(message = "End time is required")
    private LocalTime endTime;
}