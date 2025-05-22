package com.investhoodit.RevisionHub.repository;

import com.investhoodit.RevisionHub.model.Schedule;
import com.investhoodit.RevisionHub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByIdAndUser(Long id, User user);
    boolean existsByIdAndUser(Long id, User user);
    void deleteByIdAndUser(Long id, User user);
    boolean existsByUserAndSubjectAndDayOfWeekAndStartTime(
            User user, String subject, String dayOfWeek, LocalTime startTime);
    List<Schedule> findAllByUser(User user);
}