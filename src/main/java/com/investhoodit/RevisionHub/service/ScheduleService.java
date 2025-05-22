package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.ScheduleRequest;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.Schedule;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.ScheduleRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<ApiResponse> saveSchedule(ScheduleRequest scheduleRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check for existing schedule
        if (scheduleRepository.existsByUserAndSubjectAndDayOfWeekAndStartTime(
                user, scheduleRequest.getSubject(), scheduleRequest.getDayOfWeek(), scheduleRequest.getStartTime())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Schedule already exists for this subject and time", false, null));
        }

        // Validate time range
        if (scheduleRequest.getStartTime().isAfter(scheduleRequest.getEndTime())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Start time must be before end time", false, null));
        }

        Schedule schedule = new Schedule();
        schedule.setUser(user);
        schedule.setSubject(scheduleRequest.getSubject());
        schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
        schedule.setStartTime(scheduleRequest.getStartTime());
        schedule.setEndTime(scheduleRequest.getEndTime());

        scheduleRepository.save(schedule);

        return ResponseEntity.status(201)
                .body(new ApiResponse("Schedule created successfully", true, schedule));
    }

    public ResponseEntity<ApiResponse> getSchedule() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Schedule> schedules = scheduleRepository.findAllByUser(user);
        return ResponseEntity.ok(new ApiResponse("Schedules retrieved successfully", true, schedules));
    }

    public ResponseEntity<ApiResponse> deleteSchedule(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Schedule schedule;

        if (!scheduleRepository.existsByIdAndUser(id, user)) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse("Schedule not found", false, null));
        }else {
            schedule = scheduleRepository.findByIdAndUser(id, user)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));
        }

        try {
            scheduleRepository.delete(schedule);
            return ResponseEntity.status(204).body(new ApiResponse("Schedule deleted successfully", true, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse("Failed to delete schedule: " + e.getMessage(), false, null));
        }
    }

    public ResponseEntity<ApiResponse> updateSchedule(ScheduleRequest scheduleRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Assuming ScheduleRequest includes scheduleId
        Schedule schedule = scheduleRepository.findByIdAndUser(scheduleRequest.getScheduleId(), user)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // Validate time range
        if (scheduleRequest.getStartTime().isAfter(scheduleRequest.getEndTime())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Start time must be before end time", false, null));
        }

        schedule.setSubject(scheduleRequest.getSubject());
        schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
        schedule.setStartTime(scheduleRequest.getStartTime());
        schedule.setEndTime(scheduleRequest.getEndTime());

        scheduleRepository.save(schedule);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Schedule updated successfully", true, schedule));
    }
}
