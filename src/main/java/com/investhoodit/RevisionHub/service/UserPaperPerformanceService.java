package com.investhoodit.RevisionHub.service;

import com.investhoodit.RevisionHub.dto.PerformanceRequest;
import com.investhoodit.RevisionHub.dto.SubjectMasteryDTO;
import com.investhoodit.RevisionHub.model.DigitizedQuestionPaper;
import com.investhoodit.RevisionHub.model.PerformanceMetric;
import com.investhoodit.RevisionHub.model.User;
import com.investhoodit.RevisionHub.model.UserPaperPerformance;
import com.investhoodit.RevisionHub.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPaperPerformanceService {
    private final PerformanceMetricRepository performanceRepository;
    private final UserRepository userRepository;
    private final DigitizedQuestionPaperRepository paperRepository;
    private final DataMigrationService dataMigrationService;
    private final SubjectMasteryRepository subjectMasteryRepository;

    public UserPaperPerformanceService(PerformanceMetricRepository performanceRepository, UserRepository userRepository, DigitizedQuestionPaperRepository paperRepository, DataMigrationService dataMigrationService, SubjectMasteryRepository subjectMasteryRepository) {
        this.performanceRepository = performanceRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
        this.dataMigrationService = dataMigrationService;
        this.subjectMasteryRepository = subjectMasteryRepository;
    }

    public PerformanceMetric recordAttempt(PerformanceRequest request) {
        User user = userRepository.findByEmail(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        DigitizedQuestionPaper paper = paperRepository.findById(request.getPaperId())
                .orElseThrow(() -> new EntityNotFoundException("Paper not found"));
        PerformanceMetric performance;
        DecimalFormat df = new DecimalFormat("#.##");
        double score = Double.parseDouble(df.format(request.getScore()));

        if (performanceRepository.existsByUserIdAndActivityId(user.getId(), request.getPaperId())) {
            performance = performanceRepository.findByUserIdAndActivityId(user.getId(), request.getPaperId());
            performance.setScore(performance.getScore() > score?performance.getScore():score);
            performance.setDate(LocalDate.now());
        } else {
            performance = new PerformanceMetric();
            performance.setUser(user);
            performance.setSubject(paper.getSubject());
            performance.setScore(score);
            performance.setActivityName(paper.getFileName());
            performance.setActivityType("Digitized QP");
            performance.setActivityId(paper.getId());
            performance.setDate(LocalDate.now());

        }
        //Trigger migration for the user
        dataMigrationService.migrateSubjectsForUser(user);

        return performanceRepository.save(performance);
    }

    public List<UserPaperPerformance> getUserPerformance() {
        return performanceRepository.findByUserId(findByToken().getId());
    }



    public long getCompletedTasksCount() {
        User user = findByToken();
        return performanceRepository.countByUserId(user.getId());
    }

    public List<SubjectMasteryDTO> getSubjectProgress() {
        User user = findByToken();
        List<PerformanceMetric> metrics = performanceRepository.findByUser(user);

        // Group by subject and compute average score
        return metrics.stream()
                .collect(Collectors.groupingBy(
                        metric -> metric.getSubject().getSubjectName(),
                        Collectors.averagingDouble(PerformanceMetric::getScore)
                ))
                .entrySet().stream()
                .map(entry -> new SubjectMasteryDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }


    public User findByToken() {
        return userRepository.findByEmail(
                        SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

}
