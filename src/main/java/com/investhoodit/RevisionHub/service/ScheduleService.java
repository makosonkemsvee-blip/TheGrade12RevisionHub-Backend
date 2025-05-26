package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.ScheduleRequest;
import com.investhoodit.RevisionHub.model.ApiResponse;
import com.investhoodit.RevisionHub.model.QuestionPaper;
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

    public ResponseEntity<ApiResponse<Schedule>> saveSchedule(ScheduleRequest scheduleRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check for existing schedule
        if (scheduleRepository.existsByUserAndSubjectAndDayOfWeekAndStartTime(
                user, scheduleRequest.getSubject(), scheduleRequest.getDayOfWeek(), scheduleRequest.getStartTime())) {

            ApiResponse<Schedule> response = new ApiResponse<>(
                    false,
                    "Schedule already exists for this subject and time",
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        }

        // Validate time range
        if (scheduleRequest.getStartTime().isAfter(scheduleRequest.getEndTime())) {
            ApiResponse<Schedule> response = new ApiResponse<>(
                    false,
                    "Start time must be before end time",
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        }

        Schedule schedule = new Schedule();
        schedule.setUser(user);
        schedule.setSubject(scheduleRequest.getSubject());
        schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
        schedule.setStartTime(scheduleRequest.getStartTime());
        schedule.setEndTime(scheduleRequest.getEndTime());

        scheduleRepository.save(schedule);

        ApiResponse<Schedule> response = new ApiResponse<>(
                true,
                "Schedule created successfully",
                schedule
        );
        return ResponseEntity.status(201)
                .body(response);
    }

    public ResponseEntity<ApiResponse<List<Schedule>>> getSchedule() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Schedule> schedules = scheduleRepository.findAllByUser(user);

        ApiResponse<List<Schedule>> response = new ApiResponse<>(
                true,
                "Schedules retrieved successfully",
                schedules
        );
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<ApiResponse<Schedule>> deleteSchedule(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Schedule schedule;

        if (!scheduleRepository.existsByIdAndUser(id, user)) {
            ApiResponse<Schedule> response = new ApiResponse<>(
                    false,
                    "Schedule not found",
                    null
            );
            return ResponseEntity.status(404)
                    .body(response);
        }else {
            schedule = scheduleRepository.findByIdAndUser(id, user)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));
        }

        try {
            scheduleRepository.delete(schedule);
            ApiResponse<Schedule> response = new ApiResponse<>(
                    true,
                    "Schedule deleted successfully",
                    null
            );
            return ResponseEntity.status(204).body(response);
        } catch (Exception e) {
            ApiResponse<Schedule> response = new ApiResponse<>(
                    false,
                    "Failed to delete schedule: " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(500)
                    .body(response);
        }
    }

    public ResponseEntity<ApiResponse<Schedule>> updateSchedule(ScheduleRequest scheduleRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Schedule schedule = scheduleRepository.findByIdAndUser(scheduleRequest.getScheduleId(), user)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // Validate time range
        if (scheduleRequest.getStartTime().isAfter(scheduleRequest.getEndTime())) {
            ApiResponse<Schedule> response = new ApiResponse<>(
                    false,
                    "Start time must be before end time",
                    null
            );
            return ResponseEntity.badRequest()
                    .body(response);
        }

        schedule.setSubject(scheduleRequest.getSubject());
        schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
        schedule.setStartTime(scheduleRequest.getStartTime());
        schedule.setEndTime(scheduleRequest.getEndTime());

        scheduleRepository.save(schedule);

        ApiResponse<Schedule> response = new ApiResponse<>(
                true,
                "Schedule updated successfully",
                schedule
        );

        return ResponseEntity.status(200)
                .body(response);
    }
}
