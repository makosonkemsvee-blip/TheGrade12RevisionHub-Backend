package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PasswordChangeDTO;
import com.investhoodit.RevisionHub.dto.RegisterRequestDTO;
import com.investhoodit.RevisionHub.model.Attendance;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.repository.AttendanceRepository;
import com.investhoodit.RevisionHub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final AttendanceRepository attendanceRepository;

    public UserService(UserRepository userRepository, PasswordEncoderService passwordEncoderService, AttendanceRepository attendanceRepository) {
        this.userRepository = userRepository;
        this.passwordEncoderService = passwordEncoderService;
        this.attendanceRepository = attendanceRepository;
    }

    public void changePassword(UserDetails userDetails, PasswordChangeDTO passwordChangeDTO) {
        log.info("Attempting password change for user: {}", userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userDetails.getUsername());
                    return new EntityNotFoundException("User not found with email: " + userDetails.getUsername());
                });

        // Verify current password
        if (!passwordEncoderService.verifyPassword(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            log.warn("Current password is incorrect for user: {}", userDetails.getUsername());
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        String encodedNewPassword = passwordEncoderService.encodePassword(passwordChangeDTO.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", userDetails.getUsername());
    }

    public void saveProfile(String email, Map<String, Object> profileData) {
        log.info("Saving profile for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });
        // Update user fields based on profileData
        if (profileData.containsKey("username")) {
            user.setFirstName((String) profileData.get("username"));
        }
        if (profileData.containsKey("email")) {
            user.setEmail((String) profileData.get("email"));
        }
        // Add other fields as needed
        userRepository.save(user);
        log.info("Profile saved successfully for user: {}", email);
    }

    public User searchUser(String email){

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

    }

    public List<User> getAllStudents() {

        return userRepository.findAll();

    }

    public boolean removeUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if(user != null) {
            userRepository.delete(user);
            return true;
        }else{
            return false;
        }

    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email); // Assumes UserRepository has this method
     }
    public long countStudents(){
        return userRepository.countByRole("USER");
    }

    public User register(RegisterRequestDTO request) {
        log.info("Registering new user: {}", request.getEmail());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoderService.encodePassword(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole("USER");
        user.setFirstLogin(true);
        user = userRepository.save(user);

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDateSignedUp(LocalDate.now());
        attendance.setCountLogin(0);
        attendanceRepository.save(attendance);

        log.info("User registered successfully: {}", request.getEmail());
        return user;
    }

    public void recordLogin(String email) {
        log.info("Recording login for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });

        Attendance attendance = attendanceRepository.findByUser(user)
                .orElseGet(() -> {
                    Attendance newAttendance = new Attendance();
                    newAttendance.setUser(user);
                    newAttendance.setDateSignedUp(LocalDate.now());
                    newAttendance.setCountLogin(0);
                    return newAttendance;
                });

        LocalDateTime now = LocalDateTime.now();
        if (attendance.getLastLogin() == null ||
                ChronoUnit.HOURS.between(attendance.getLastLogin(), now) >= 24) {
            attendance.setCountLogin(attendance.getCountLogin() + 1);
            attendance.setLastLogin(now);
            attendanceRepository.save(attendance);
            log.info("Login recorded for user: {}, countLogin: {}, lastLogin: {}",
                    email, attendance.getCountLogin(), now);
        } else {
            log.info("Login not recorded for user: {}, less than 24 hours since last login: {}",
                    email, attendance.getLastLogin());
        }
    }

    public double getAttendancePercentage(String email) {
        log.info("Calculating attendance percentage for user: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new EntityNotFoundException("User not found with email: " + email);
                });

        Attendance attendance = attendanceRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Attendance record not found for user: {}", email);
                    return new EntityNotFoundException("Attendance record not found for user: " + email);
                });

        long days = ChronoUnit.DAYS.between(attendance.getDateSignedUp(), LocalDate.now()) + 1;
        double percentage = days > 0 ? Math.round((double) attendance.getCountLogin() / days * 100) : 0.0;
        log.info("Attendance for user {}: countLogin={}, days={}, percentage={}%",
                email, attendance.getCountLogin(), days, percentage);
        return percentage;
    }


}