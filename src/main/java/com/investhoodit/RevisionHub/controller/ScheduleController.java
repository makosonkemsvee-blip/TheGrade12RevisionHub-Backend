package com.investhoodit.RevisionHub.controller;

import com.investhoodit.RevisionHub.dto.ScheduleRequest;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/get-schedules")
    public ResponseEntity<ApiResponse> getSchedules() {
        return scheduleService.getSchedule();
    }

    @PostMapping("/create-schedule")
    public ResponseEntity<ApiResponse> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        return scheduleService.saveSchedule(scheduleRequest);
    }

    @PutMapping("/update-schedule")
    public ResponseEntity<ApiResponse> updateSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        return scheduleService.updateSchedule(scheduleRequest);
    }

    @DeleteMapping("/delete-schedule/{scheduleId}")
    public ResponseEntity<ApiResponse> deleteSchedule(@PathVariable Long scheduleId) {
        return scheduleService.deleteSchedule(scheduleId);
    }
}
