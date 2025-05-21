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

        Schedule schedule = new Schedule();
        schedule.setUser(user);
        schedule.setSubject(scheduleRequest.getSubject());
        schedule.setDayOfWeek(scheduleRequest.getDayOfWeek());
        schedule.setStartTime(scheduleRequest.getStartTime());
        schedule.setEndTime(scheduleRequest.getEndTime());

        scheduleRepository.save(schedule);

        return ResponseEntity.status(200).body(new ApiResponse("Schedule uploaded successfully",true,schedule));
    }

    public ResponseEntity<ApiResponse> deleteSchedule(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        scheduleRepository.deleteByIdAndUser(id,user);

        return ResponseEntity.status(200).body(new ApiResponse("Schedule deleted successfully",true,null));
    }

    public ResponseEntity<ApiResponse> updateSchedule(ScheduleRequest scheduleRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // retrieve the schedule based on the user and schedule id

        return ResponseEntity.status(200).body(new ApiResponse("Schedule updated successfully",true,null));
    }
}
